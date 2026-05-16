package com.shopee.shopee_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ShopeeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopeeBackendApplication.class, args);
	}

}
