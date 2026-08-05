package com.localfix.user.service;

import com.localfix.user.dto.request.ChangePasswordRequest;
import com.localfix.user.dto.request.UpdateProfileRequest;
import com.localfix.user.dto.response.UserProfileResponse;

public interface UserService {

    UserProfileResponse getCurrentUser();

    UserProfileResponse updateProfile(UpdateProfileRequest request);

    void changePassword(ChangePasswordRequest request);

}
