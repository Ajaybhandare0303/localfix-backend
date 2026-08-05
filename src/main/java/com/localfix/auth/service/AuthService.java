package com.localfix.auth.service;

import com.localfix.auth.dto.request.LoginRequest;
import com.localfix.auth.dto.request.RegisterRequest;
import com.localfix.auth.dto.response.LoginResponse;
import com.localfix.auth.dto.response.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}