package com.localfix.service.controller;

import com.localfix.common.response.ApiResponse;
import com.localfix.service.dto.request.CreateServiceRequest;
import com.localfix.service.dto.request.UpdateServiceRequest;
import com.localfix.service.dto.response.ServiceResponse;
import com.localfix.service.service.ServiceManagementService;
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
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Validated
public class ServiceController {

    private final ServiceManagementService serviceManagementService;

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceResponse>> createService(
            @Valid @RequestBody CreateServiceRequest request) {
        ServiceResponse response =
                serviceManagementService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<ServiceResponse>builder()
                                .success(true)
                                .message("Service created successfully.")
                                .data(response)
                                .build()
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ServiceResponse>>> getAllServices(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be 0 or greater")
            int page,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size
            ) {
        PageResponse<ServiceResponse> response =
                serviceManagementService.getAll(page, size);
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<ServiceResponse>>builder()
                        .success(true)
                        .message("Services fetched successfully.")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceResponse>> getServiceById(
            @PathVariable UUID id) {
        ServiceResponse response =
                serviceManagementService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.<ServiceResponse>builder()
                        .success(true)
                        .message("Service fetched successfully.")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PageResponse<ServiceResponse>>>
    getServicesByCategory(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be 0 or greater")
            int page,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size,
            @PathVariable UUID categoryId) {
PageResponse<ServiceResponse> response =
                serviceManagementService
                        .getByCategory(categoryId,page,size);
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<ServiceResponse>>builder()
                        .success(true)
                        .message(
                                "Services fetched by category successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceResponse>> updateService(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateServiceRequest request) {
        ServiceResponse response =
                serviceManagementService.update(id, request);
        return ResponseEntity.ok(
                ApiResponse.<ServiceResponse>builder()
                        .success(true)
                        .message("Service updated successfully.")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteService(
            @PathVariable UUID id) {
        serviceManagementService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Service deactivated successfully.")
                        .build()
        );
    }

    @GetMapping("/reactive/{id}")
    public ResponseEntity<ApiResponse<Void>> reActiveService(
            @PathVariable UUID id) {
        serviceManagementService.reActive(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Service reactivated successfully.")
                        .build()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ServiceResponse>>>
    searchServices(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be 0 or greater")
            int page,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size)
    {
        PageResponse<ServiceResponse> response =
                serviceManagementService.search(
                        keyword,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<PageResponse<ServiceResponse>>builder()
                        .success(true)
                        .message(
                                "Services searched successfully."
                        )
                        .data(response)
                        .build()
        );
    }

}