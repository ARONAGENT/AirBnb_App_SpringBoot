package com.majorproject.airbnbApp.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.majorproject.airbnbApp.dtos.BookingDto;
import com.majorproject.airbnbApp.dtos.BookingRequest;
import com.majorproject.airbnbApp.dtos.GuestDto;
import com.majorproject.airbnbApp.entities.*;
import com.majorproject.airbnbApp.entities.enums.BookingStatus;
import com.majorproject.airbnbApp.exceptions.ResourceNotFoundException;
import com.majorproject.airbnbApp.exceptions.UnAuthorisedException;
import com.majorproject.airbnbApp.repositories.*;
import com.majorproject.airbnbApp.services.BookingService;
import com.majorproject.airbnbApp.services.CheckoutService;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {


    @Value("${frontend.url}")
    private String frontendUrl;

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final GuestRepository guestRepository;
    private final CheckoutService checkoutService;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public BookingDto initializedBooking(BookingRequest bookingRequest) {

        log.info("Initialing booking for hotel : {} , room : {} , Date to : {} to {}",bookingRequest.getHotelId(),bookingRequest.getRoomId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate());

        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found by ID: " + bookingRequest.getHotelId()));

        Room room = roomRepository
                .findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+bookingRequest.getRoomId()));

        List<Inventory> inventoryList=inventoryRepository.findAndLockAvailableInventory(room.getId(),
                bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(),bookingRequest.getRoomsCount());

        long daysCount= ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate())+1;

        if(inventoryList.size() != daysCount){
            throw  new IllegalStateException("Room is not available");
        }

        // Reserved the room

        for (Inventory inventory: inventoryList){
            inventory.setReservedCount(inventory.getReservedCount()+bookingRequest.getRoomsCount());
        }
        inventoryRepository.saveAll(inventoryList);


        //create  user// Calculate Dynamic Price ... later

        // Initialized Booking
        User user=getCurrentUser();
        Booking booking= Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(user)
                .roomCount(bookingRequest.getRoomsCount())
                .amount(BigDecimal.TEN)
                .build();

        bookingRepository.save(booking);

       return modelMapper.map(booking,BookingDto.class);
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestIdList) {

        log.info("Adding guests for booking with id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found with id: "+bookingId));
        User user = getCurrentUser();

        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests");
        }

        for (GuestDto guestdto: guestIdList) {
           Guest guest=modelMapper.map(guestdto,Guest.class);
           guest.setUser(user);
           guest=guestRepository.save(guest);
           booking.getGuests().add(guest);

        }

        booking.setBookingStatus(BookingStatus.GUEST_ADDED);
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public String initiatePayments(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()-> new ResourceNotFoundException("Booking Id Not Found With id"+bookingId));
        User user= getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }

      String sessionUrl=  checkoutService.getCheckOutSession(booking,frontendUrl+"/payments/success",frontendUrl+"/payments/failure");

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;

    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if ("checkout.session.completed".equals(event.getType())) {
//            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            Session session = retrieveSessionFromEvent(event);
            if (session == null) return;

            String sessionId = session.getId();
            Booking booking =
                    bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() ->
                            new ResourceNotFoundException("Booking not found for session ID: "+sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomCount());

            inventoryRepository.confirmBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomCount());

            log.info("Successfully confirmed the booking for Booking ID: {}", booking.getId());
        } else {
            log.warn("Unhandled event type: {}", event.getType());
        }
    }

    private Session retrieveSessionFromEvent(Event event) {
        log.info("inside  retrieveSessionFromEvent");
        try {

            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            if (deserializer.getObject().isPresent()) {
                return (Session) deserializer.getObject().get();
            } else {
                String rawJson = event.getData().getObject().toJson();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(rawJson);
                String sessionId = jsonNode.get("id").asText();

                return Session.retrieve(sessionId);
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to retrieve session data");
        }
    }

    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
