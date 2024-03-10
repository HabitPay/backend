package com.habitpay.habitpay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HabitpayApplication {

	public static void main(String[] args) {
		SpringApplication.run(HabitpayApplication.class, args);
	}

}
