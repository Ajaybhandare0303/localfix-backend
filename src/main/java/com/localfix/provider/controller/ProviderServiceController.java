package com.localfix.provider.controller;

import com.localfix.common.response.ApiResponse;
import com.localfix.provider.service.ProviderServiceMappingService;

import com.localfix.provider.services.dto.request.AddProviderServiceRequest;
import com.localfix.provider.services.dto.response.ProviderServiceResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/providers/me/services")
@RequiredArgsConstructor
public class ProviderServiceController {

    private final ProviderServiceMappingService
            providerServiceMappingService;


    @PostMapping
    public ResponseEntity<
            ApiResponse<ProviderServiceResponse>>
    addService(
            @Valid
            @RequestBody
            AddProviderServiceRequest request) {

        ProviderServiceResponse response =
                providerServiceMappingService
                        .addService(request);

        return ResponseEntity.ok(
                ApiResponse
                        .<ProviderServiceResponse>builder()
                        .success(true)
                        .message(
                                "Service added to provider successfully."
                        )
                        .data(response)
                        .build()
        );
    }


    @GetMapping
    public ResponseEntity<
            ApiResponse<List<ProviderServiceResponse>>>
    getMyServices() {

        List<ProviderServiceResponse> response =
                providerServiceMappingService
                        .getMyServices();

        return ResponseEntity.ok(
                ApiResponse
                        .<List<ProviderServiceResponse>>builder()
                        .success(true)
                        .message(
                                "Provider services fetched successfully."
                        )
                        .data(response)
                        .build()
        );
    }


    @DeleteMapping("/{serviceId}")
    public ResponseEntity<ApiResponse<Void>>
    removeService(
            @PathVariable UUID serviceId) {

        providerServiceMappingService
                .removeService(serviceId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(
                                "Service removed from provider successfully."
                        )
                        .build()
        );
    }
}