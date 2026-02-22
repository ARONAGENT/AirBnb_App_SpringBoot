package com.majorproject.airbnbApp.services.impl;

import com.majorproject.airbnbApp.dtos.ProfileUpdateRequestDto;
import com.majorproject.airbnbApp.dtos.UserDto;
import com.majorproject.airbnbApp.entities.User;
import com.majorproject.airbnbApp.exceptions.ResourceNotFoundException;
import com.majorproject.airbnbApp.repositories.UserRepository;
import com.majorproject.airbnbApp.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.majorproject.airbnbApp.utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private static final String USER_CACHE = "users";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public User getUserById(Long id) {
        log.info("Request received to fetch user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id not found by id" + id));
        log.info("User fetched successfully for id: {}", id);
        return user;
    }

    @Override
    @Transactional
    @CachePut(value = USER_CACHE, key = "#result.id")
    public void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto) {
        User user = getCurrentUser();
        log.info("Updating profile for user id: {}", user.getId());

        if(profileUpdateRequestDto.getDateOfBirth() != null) {
            user.setDateOfBirth(profileUpdateRequestDto.getDateOfBirth());
            log.info("Updated dateOfBirth for user id: {}", user.getId());
        }
        if(profileUpdateRequestDto.getGender() != null) {
            user.setGender(profileUpdateRequestDto.getGender());
            log.info("Updated gender for user id: {}", user.getId());
        }
        if (profileUpdateRequestDto.getName() != null) {
            user.setName(profileUpdateRequestDto.getName());
            log.info("Updated name for user id: {}", user.getId());
        }

        userRepository.save(user);
        log.info("Profile saved successfully for user id: {}", user.getId());
    }

    @Override
    @Cacheable(value = USER_CACHE, key = "'myProfile_' + T(com.majorproject.airbnbApp.utils.AppUtils).getCurrentUser().getId()")
    public UserDto getMyProfile() {
        User user = getCurrentUser();
        log.info("Getting the profile for user with id: {}", user.getId());
        UserDto userDto = modelMapper.map(user, UserDto.class);
        log.info("Profile mapped to DTO for user id: {}", user.getId());
        return userDto;
    }

    @Override
    @CacheEvict(value = USER_CACHE, allEntries = false)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username/email: {}", username);
        UserDetails userDetails = userRepository.findByEmail(username).orElse(null);
        log.info("UserDetails loaded for username/email: {}", username);
        return userDetails;
    }
}