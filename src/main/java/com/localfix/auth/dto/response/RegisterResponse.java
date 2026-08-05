package com.localfix.auth.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Builder
public record RegisterResponse(

        UUID userId,

        String firstName,

        String lastName,

        String email,

        String mobile,

        String message



) {

}