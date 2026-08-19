package com.localfix.provider.service;
import com.localfix.provider.services.dto.request.AddProviderServiceRequest;
import com.localfix.provider.services.dto.response.ProviderServiceResponse;

import java.util.List;
import java.util.UUID;

public interface ProviderServiceMappingService {

    ProviderServiceResponse addService(
            AddProviderServiceRequest request
    );

    List<ProviderServiceResponse> getMyServices();

    void removeService(UUID serviceId);
}