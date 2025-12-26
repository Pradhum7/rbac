package com.company.rbac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RbacApplication {
	public static void main(String[] args) {
		SpringApplication.run(RbacApplication.class, args);
	}
}