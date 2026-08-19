package com.localfix.service.service;

import com.localfix.service.dto.request.CreateServiceRequest;
import com.localfix.service.dto.request.UpdateServiceRequest;
import com.localfix.service.dto.response.ServiceResponse;
import com.localfix.servicecategory.dto.response.PageResponse;

import java.util.UUID;

public interface ServiceManagementService {

    ServiceResponse create(
            CreateServiceRequest request
    );

    ServiceResponse update(
            UUID id,
            UpdateServiceRequest request
    );

    ServiceResponse getById(UUID id);

    PageResponse<ServiceResponse> getAll(
            int page,
            int size
    );

    PageResponse<ServiceResponse> search(
            String keyword,
            int page,
            int size
    );

    PageResponse<ServiceResponse> getByCategory(
            UUID categoryId,
            int page,
            int size
    );

    void delete(UUID id);

    public void reActive(UUID id);
}