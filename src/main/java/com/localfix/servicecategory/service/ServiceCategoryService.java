package com.localfix.servicecategory.service;
import com.localfix.servicecategory.dto.request.CreateCategoryRequest;
import com.localfix.servicecategory.dto.request.UpdateCategoryRequest;
import com.localfix.servicecategory.dto.response.CategoryResponse;
import com.localfix.servicecategory.entity.ServiceCategory;

import java.util.List;
import java.util.UUID;

public interface ServiceCategoryService {

    CategoryResponse create(CreateCategoryRequest request);

    CategoryResponse update(UUID id, UpdateCategoryRequest request);

    CategoryResponse getById(UUID id);

    List<CategoryResponse> getAll();

    void delete(UUID id);
}
