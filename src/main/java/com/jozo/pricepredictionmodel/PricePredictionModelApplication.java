package com.jozo.pricepredictionmodel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication()
public class PricePredictionModelApplication {

	public static void main(String[] args) {
		SpringApplication.run(PricePredictionModelApplication.class, args);
	}

}
