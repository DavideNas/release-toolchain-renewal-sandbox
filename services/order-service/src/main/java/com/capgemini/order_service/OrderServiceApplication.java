package com.capgemini.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SpringBootApplication
@RestController
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@GetMapping("/api/orders")
	public Map<String, String> getOrders() {
		return Map.of(
			"status", "ALIVE",
			"message", "Order Service temporaneo per simulazione pipeline",
			"wave", "1"
		);
	}

}
