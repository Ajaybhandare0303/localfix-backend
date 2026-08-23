package com.localfix.admin.service;

import com.localfix.provider.entity.Provider;

import java.util.UUID;

public interface AdminService {

    Provider verifyProvider(UUID providerId);

}
