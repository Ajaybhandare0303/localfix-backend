package com.localfix.provider.service;

import com.localfix.provider.dto.request.CreateProviderRequest;
import com.localfix.provider.dto.request.ProviderStatusRequest;
import com.localfix.provider.dto.request.UpdateProviderRequest;
import com.localfix.provider.dto.response.ProviderListResponse;
import com.localfix.provider.dto.response.ProviderResponse;
import com.localfix.servicecategory.dto.response.PageResponse;

import java.util.UUID;

public interface ProviderService {

    PageResponse<ProviderListResponse> getAllProviders(
            int page,
            int size
    );

    ProviderResponse getProviderById(
            UUID providerId
    );

    PageResponse<ProviderListResponse> getProvidersByService(
            UUID serviceId,
            int page,
            int size
    );

    PageResponse<ProviderListResponse> getProvidersByCategory(
            UUID categoryId,
            int page,
            int size
    );

    ProviderResponse updateMyStatus(ProviderStatusRequest request);

    ProviderResponse updateMyProfile(UpdateProviderRequest request);

    ProviderResponse create(CreateProviderRequest request);

    ProviderResponse getMyProfile();

    PageResponse<ProviderListResponse> searchProviders(
            String keyword,
            String city,
            String state,
            UUID serviceId,
            UUID categoryId,
            int page,
            int size
    );

}