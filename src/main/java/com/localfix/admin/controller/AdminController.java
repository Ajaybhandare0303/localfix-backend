package com.localfix.admin.controller;

import com.localfix.admin.service.AdminService;
import com.localfix.booking.dto.response.BookingStatusHistoryResponse;
import com.localfix.common.response.ApiResponse;
import com.localfix.provider.entity.Provider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(
            summary = "Provider verification",
            description = "Verified a new provider."
    )
    @PostMapping("/provider/verified/{providerId}")
    public ResponseEntity<ApiResponse<Provider>> verifiedProvider(@PathVariable("providerId")UUID providerId)
    {
        Provider provider=adminService.verifyProvider(providerId);

        return ResponseEntity.ok(
                ApiResponse
                        .<Provider>builder()
                        .success(true)
                        .message(
                                "Provider verified successfully."
                        )
                        .data(provider)
                        .build()
        );
    }

}
