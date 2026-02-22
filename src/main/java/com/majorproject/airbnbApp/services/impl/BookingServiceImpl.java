package com.majorproject.airbnbApp.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.majorproject.airbnbApp.dtos.BookingDto;
import com.majorproject.airbnbApp.dtos.BookingRequest;
import com.majorproject.airbnbApp.dtos.GuestDto;
import com.majorproject.airbnbApp.dtos.HotelReportDto;
import com.majorproject.airbnbApp.entities.*;
import com.majorproject.airbnbApp.entities.enums.BookingStatus;
import com.majorproject.airbnbApp.exceptions.ResourceNotFoundException;
import com.majorproject.airbnbApp.exceptions.UnAuthorisedException;
import com.majorproject.airbnbApp.repositories.*;
import com.majorproject.airbnbApp.services.BookingService;
import com.majorproject.airbnbApp.services.CheckoutService;
import com.majorproject.airbnbApp.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final PricingService pricingService;

    private static final String BOOKING_CACHE = "bookings";

    @Override
    @Transactional
    @CacheEvict(value = BOOKING_CACHE, allEntries = true)
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


        // Reserve the room/ update the booked count of inventories
        inventoryRepository.initBooking(room.getId(), bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

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
                .amount(totalPrice)
                .build();

        bookingRepository.save(booking);

       return modelMapper.map(booking,BookingDto.class);
    }

    @Override
    @Transactional
    @CacheEvict(value = BOOKING_CACHE, allEntries = true)
    public BookingDto addGuests(Long bookingId, List<Long> guestIdList) {

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

        for (Long guestId: guestIdList) {
            Guest guest = guestRepository.findById(guestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: "+guestId));
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUEST_ADDED);
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    @CacheEvict(value = BOOKING_CACHE, allEntries = true)
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
    @CacheEvict(value = BOOKING_CACHE, allEntries = true)
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

    @Override
    @Transactional
    @CacheEvict(value = BOOKING_CACHE, allEntries = true)
    public void cancelBooking(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()-> new ResourceNotFoundException("Booking Id Not Found With id"+bookingId));
        User user= getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking has already expired");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomCount());

        inventoryRepository.cancelBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomCount());


        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(refundParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    @Cacheable(value = BOOKING_CACHE, key = "'hotel_' + #hotelId")
    public List<BookingDto> getAllBookingsByHotelId(Long hotelId) {
        log.info("Request received to get all bookings for hotelId: {}", hotelId);

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not " +
                "found with ID: "+hotelId));
        User user = getCurrentUser();

        log.info("Validating ownership for userId: {} and hotelId: {}", user.getId(), hotelId);

        if(!user.equals(hotel.getOwner())) {
            log.error("Access denied for userId: {} on hotelId: {}", user.getId(), hotelId);
            throw new AccessDeniedException("You are not the owner of hotel with id: "+hotelId);
        }

        List<Booking> bookings = bookingRepository.findByHotel(hotel);
        log.info("Total bookings fetched for hotelId {}: {}", hotelId, bookings.size());

        return bookings.stream()
                .map((element) -> modelMapper.map(element, BookingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = BOOKING_CACHE, key = "'report_' + #hotelId + '_' + #startDate + '_' + #endDate")
    public HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating report for hotelId: {} between {} and {}", hotelId, startDate, endDate);

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not " +
                "found with ID: "+hotelId));
        User user = getCurrentUser();

        if(!user.equals(hotel.getOwner())) {
            log.error("Unauthorized report access attempt by userId: {} for hotelId: {}", user.getId(), hotelId);
            throw new AccessDeniedException("You are not the owner of hotel with id: "+hotelId);
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findByHotelAndCreatedAtBetween(hotel, startDateTime, endDateTime);

        Long totalConfirmedBookings = bookings
                .stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();

        BigDecimal totalRevenueOfConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgRevenue = totalConfirmedBookings == 0 ? BigDecimal.ZERO :
                totalRevenueOfConfirmedBookings.divide(BigDecimal.valueOf(totalConfirmedBookings), RoundingMode.HALF_UP);

        log.info("Report generated for hotelId: {} | Confirmed: {} | Revenue: {}", hotelId, totalConfirmedBookings, totalRevenueOfConfirmedBookings);

        return new HotelReportDto(totalConfirmedBookings, totalRevenueOfConfirmedBookings, avgRevenue);
    }

    @Override
    @Cacheable(value = BOOKING_CACHE, key = "'myBookings_' + T(com.majorproject.airbnbApp.utils.AppUtils).getCurrentUser().getId()")
    public List<BookingDto> getMyBookings() {
        User user = getCurrentUser();
        log.info("Fetching bookings for userId: {}", user.getId());

        return bookingRepository.findByUser(user)
                .stream()
                .map(booking -> BookingDto.builder()
                        .id(booking.getId())
                        .bookingStatus(booking.getBookingStatus())
                        .createdAt(booking.getCreatedAt())
                        .updatedAt(booking.getUpdatedAt())
                        .amount(booking.getAmount())
                        .checkInDate(booking.getCheckInDate())
                        .checkOutDate(booking.getCheckOutDate())
                        .roomCount(booking.getRoomCount())
                        .hotelName(booking.getHotel().getName())
                        .hotelCity(booking.getHotel().getCity())
                        .roomType(booking.getRoom().getType())
                        .guests(
                                booking.getGuests().stream()
                                        .map(guest -> GuestDto.builder()
                                                .id(guest.getId())
                                                .name(guest.getName())
                                                .gender(String.valueOf(guest.getGender()))
                                                .age(guest.getAge())
                                                .build())
                                        .collect(Collectors.toSet())
                        )
                        .build()
                ).toList();
    }

    @Override
    @Cacheable(value = BOOKING_CACHE, key = "'booking_' + #id")
    public BookingDto getMyBookingsById(Long id) {
        log.info("Fetching booking by id: {}", id);

        User user = getCurrentUser();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id : "+id));

        if (!booking.getUser().getId().equals(user.getId())) {
            log.error("Unauthorized access attempt for bookingId: {} by userId: {}", id, user.getId());
            throw new RuntimeException("You are not allowed to view this booking");
        }

        return modelMapper.map(booking,BookingDto.class);
    }

    @Override
    @Cacheable(value = BOOKING_CACHE, key = "'status_' + #bookingId")
    public BookingStatus getBookingStatus(Long bookingId) {
        log.info("Fetching booking status for bookingId: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getCurrentUser();

        if (!user.equals(booking.getUser())) {
            log.error("Unauthorized status check attempt by userId: {} for bookingId: {}", user.getId(), bookingId);
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        log.info("Booking status fetched for bookingId: {} is {}", bookingId, booking.getBookingStatus());

        return booking.getBookingStatus();
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
