package com.localfix.provider.controller;

import com.localfix.common.response.ApiResponse;
import com.localfix.provider.dto.request.CreateProviderRequest;
import com.localfix.provider.dto.request.ProviderStatusRequest;
import com.localfix.provider.dto.request.UpdateProviderRequest;
import com.localfix.provider.dto.response.ProviderListResponse;
import com.localfix.provider.dto.response.ProviderResponse;
import com.localfix.provider.service.ProviderService;
import com.localfix.servicecategory.dto.response.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
@Validated
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProviderResponse>>
    createProvider(
            @Valid @RequestBody CreateProviderRequest request) {

        ProviderResponse response =
                providerService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<ProviderResponse>builder()
                                .success(true)
                                .message(
                                        "Provider profile created successfully."
                                )
                                .data(response)
                                .build()
                );

    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProviderResponse>>
    getMyProfile() {

        ProviderResponse response =
                providerService.getMyProfile();

        return ResponseEntity.ok(
                ApiResponse.<ProviderResponse>builder()
                        .success(true)
                        .message(
                                "Provider profile fetched successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProviderResponse>>
    updateMyProfile(
            @Valid @RequestBody UpdateProviderRequest request) {

        ProviderResponse response =
                providerService.updateMyProfile(request);

        return ResponseEntity.ok(
                ApiResponse.<ProviderResponse>builder()
                        .success(true)
                        .message(
                                "Provider profile updated successfully."
                        )
                        .data(response)
                        .build()
        );
    }
    @PatchMapping("/me/status")
    public ResponseEntity<ApiResponse<ProviderResponse>>
    updateMyStatus(
            @Valid @RequestBody ProviderStatusRequest request) {

        ProviderResponse response =
                providerService.updateMyStatus(request);

        return ResponseEntity.ok(
                ApiResponse.<ProviderResponse>builder()
                        .success(true)
                        .message(
                                "Provider status updated successfully."
                        )
                        .data(response)
                        .build()
        );
    }
    @GetMapping
    public ResponseEntity<
            ApiResponse<PageResponse<ProviderListResponse>>>
    getAllProviders(

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size) {

        PageResponse<ProviderListResponse> response =
                providerService.getAllProviders(
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<PageResponse<ProviderListResponse>>builder()
                        .success(true)
                        .message(
                                "Providers fetched successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{providerId}")
    public ResponseEntity<ApiResponse<ProviderResponse>>
    getProviderById(
            @PathVariable UUID providerId) {

        ProviderResponse response =
                providerService.getProviderById(
                        providerId
                );

        return ResponseEntity.ok(
                ApiResponse.<ProviderResponse>builder()
                        .success(true)
                        .message(
                                "Provider fetched successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<
            ApiResponse<PageResponse<ProviderListResponse>>>
    getProvidersByService(

            @PathVariable UUID serviceId,

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size) {

        PageResponse<ProviderListResponse> response =
                providerService.getProvidersByService(
                        serviceId,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<PageResponse<ProviderListResponse>>builder()
                        .success(true)
                        .message(
                                "Providers fetched by service successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<
            ApiResponse<PageResponse<ProviderListResponse>>>
    getProvidersByCategory(

            @PathVariable UUID categoryId,

            @RequestParam(defaultValue = "0")
            @Min(0)
            @Max(100)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            int size) {

        PageResponse<ProviderListResponse> response =
                providerService.getProvidersByCategory(
                        categoryId,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<PageResponse<ProviderListResponse>>builder()
                        .success(true)
                        .message(
                                "Providers fetched by category successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<
            ApiResponse<PageResponse<ProviderListResponse>>>
    searchProviders(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            String city,

            @RequestParam(required = false)
            String state,

            @RequestParam(required = false)
            UUID serviceId,

            @RequestParam(required = false)
            UUID categoryId,

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size) {

        PageResponse<ProviderListResponse> response =
                providerService.searchProviders(
                        keyword,
                        city,
                        state,
                        serviceId,
                        categoryId,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<PageResponse<ProviderListResponse>>builder()
                        .success(true)
                        .message(
                                "Providers searched successfully."
                        )
                        .data(response)
                        .build()
        );
    }

}
