package com.majorproject.airbnbApp.services;

import com.majorproject.airbnbApp.dtos.user.ProfileUpdateRequestDto;
import com.majorproject.airbnbApp.dtos.user.UserDto;
import com.majorproject.airbnbApp.entities.User;

public interface UserService {

    User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

    UserDto getMyProfile();
}
