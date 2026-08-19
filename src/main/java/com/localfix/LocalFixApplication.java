package com.localfix;

import com.localfix.common.enums.RoleType;
import com.localfix.user.role.entity.Role;
import com.localfix.user.role.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class LocalFixApplication {


	public static void main(String[] args) {

		SpringApplication.run(LocalFixApplication.class, args);

	}

}
