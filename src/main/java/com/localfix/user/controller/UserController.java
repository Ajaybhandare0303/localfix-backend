package com.localfix.user.controller;

import com.localfix.common.response.ApiResponse;
import com.localfix.user.dto.request.ChangePasswordRequest;
import com.localfix.user.dto.request.UpdateProfileRequest;
import com.localfix.user.dto.response.UserProfileResponse;
import com.localfix.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> currentUser(Authentication authentication) {

        UserProfileResponse response=userService.getCurrentUser();

        return ResponseEntity.ok(
                ApiResponse.<UserProfileResponse>builder()
                        .success(true)
                        .message("User profile fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {

        UserProfileResponse response = userService.updateProfile(request);

        return ResponseEntity.ok(
                ApiResponse.<UserProfileResponse>builder()
                        .success(true)
                        .message("Profile updated successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(request);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Password changed successfully")
                        .build()
        );
    }
}