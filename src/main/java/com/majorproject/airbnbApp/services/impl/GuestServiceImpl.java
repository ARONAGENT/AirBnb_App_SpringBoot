package com.majorproject.airbnbApp.services.impl;

import com.majorproject.airbnbApp.dtos.GuestDto;
import com.majorproject.airbnbApp.entities.Guest;
import com.majorproject.airbnbApp.entities.User;
import com.majorproject.airbnbApp.repositories.GuestRepository;
import com.majorproject.airbnbApp.services.GuestService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.majorproject.airbnbApp.utils.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;
    private static final String GUEST_CACHE = "guests";

    @Override
    @Cacheable(value = GUEST_CACHE, key = "'user_' + T(com.majorproject.airbnbApp.utils.AppUtils).getCurrentUser().getId()")
    public List<GuestDto> getAllGuests() {
        User user = getCurrentUser();
        log.info("Fetching all guests of user with id: {}", user.getId());

        List<Guest> guests = guestRepository.findByUser(user);

        return guests.stream()
                .map(guest -> GuestDto.builder()
                        .id(guest.getId())
                        .name(guest.getName())
                        .gender(String.valueOf(guest.getGender()))
                        .age(guest.getAge())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @CachePut(value = GUEST_CACHE, key = "'guest_' + #result.id")
    @CacheEvict(value = GUEST_CACHE, key = "'user_' + T(com.majorproject.airbnbApp.utils.AppUtils).getCurrentUser().getId()")
    public GuestDto addNewGuest(GuestDto guestDto) {
        log.info("Adding new guest: {}", guestDto);
        User user = getCurrentUser();
        Guest guest = modelMapper.map(guestDto, Guest.class);
        guest.setUser(user);
        Guest savedGuest = guestRepository.save(guest);
        log.info("Guest added with ID: {}", savedGuest.getId());
        return modelMapper.map(savedGuest, GuestDto.class);
    }

    @Override
    @Cacheable(value = GUEST_CACHE, key = "'guest_' + #guestId")
    public GuestDto getGuestById(Long guestId) {
        User user = getCurrentUser();
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found with id "+guestId));
        if(!user.equals(guest.getUser())) throw new AccessDeniedException("You are not the owner of this guest");
        return modelMapper.map(guest,GuestDto.class);
    }

    @Override
    @CachePut(value = GUEST_CACHE, key = "'guest_' + #guestId")
    @CacheEvict(value = GUEST_CACHE, key = "'user_' + T(com.majorproject.airbnbApp.utils.AppUtils).getCurrentUser().getId()")
    public void updateGuest(Long guestId, GuestDto guestDto) {
        log.info("Updating guest with ID: {}", guestId);
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found"));

        User user = getCurrentUser();
        if(!user.equals(guest.getUser())) throw new AccessDeniedException("You are not the owner of this guest");

        modelMapper.map(guestDto, guest);
        guest.setUser(user);
        guest.setId(guestId);

        guestRepository.save(guest);
        log.info("Guest with ID: {} updated successfully", guestId);
    }

    @Override
    @CacheEvict(value = GUEST_CACHE, key = "'guest_' + #guestId")
    public void deleteGuest(Long guestId) {
        log.info("Deleting guest with ID: {}", guestId);
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found"));

        User user = getCurrentUser();
        if(!user.equals(guest.getUser())) throw new AccessDeniedException("You are not the owner of this guest");

        guestRepository.deleteById(guestId);
        log.info("Guest with ID: {} deleted successfully", guestId);
    }
}
