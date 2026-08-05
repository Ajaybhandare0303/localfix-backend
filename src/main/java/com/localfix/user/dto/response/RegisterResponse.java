package com.localfix.user.dto.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String mobile;

    private String message;
}