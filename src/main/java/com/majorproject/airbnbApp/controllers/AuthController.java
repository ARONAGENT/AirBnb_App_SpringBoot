package com.majorproject.airbnbApp.controllers;

import com.majorproject.airbnbApp.dtos.LoginDto;
import com.majorproject.airbnbApp.dtos.LoginResponseDto;
import com.majorproject.airbnbApp.dtos.SignUpRequestDto;
import com.majorproject.airbnbApp.dtos.UserDto;
import com.majorproject.airbnbApp.security.Auth_Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
@Tag(name = "Authentication", description = "APIs for Authentication ")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final Auth_Service authService;


    @Operation(
            summary = "User signup",
            description = "Registers a new user with the provided sign-up details and returns the created user object"
    )
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        return new ResponseEntity<>(authService.signUp(signUpRequestDto), HttpStatus.CREATED);
    }

    @Operation(
            summary = "User login",
            description = "Logs in a user with email/password and returns an access token. Sets the refresh token as an HTTP-only cookie"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginDto loginDto,
                                                  HttpServletRequest httpServletRequest,
                                                  HttpServletResponse httpServletResponse) {
        String[] tokens = authService.login(loginDto);

        Cookie cookie = new Cookie("refreshToken", tokens[1]);
        cookie.setHttpOnly(true);

        httpServletResponse.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));
    }

    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using the refresh token stored in the HTTP-only cookie"
    )
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request) {
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found inside the Cookies"));

        String accessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }

}
