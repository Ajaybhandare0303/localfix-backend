package com.localfix.admin.repository;

import com.localfix.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin,UUID> {
}
