package com.localfix.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.util.UUID;

@Entity
public class Admin {

    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
