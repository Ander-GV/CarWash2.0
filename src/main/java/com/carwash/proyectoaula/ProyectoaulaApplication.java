package com.carwash.proyectoaula;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ProyectoaulaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoaulaApplication.class, args);
	}

}
