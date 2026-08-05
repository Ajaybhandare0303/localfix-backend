package com.localfix.common.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {

    private boolean success;

    private String message;

    private Map<String,String> errors;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}