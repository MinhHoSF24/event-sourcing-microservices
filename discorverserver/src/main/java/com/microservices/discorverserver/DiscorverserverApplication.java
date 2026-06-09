package com.microservices.discorverserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class DiscorverserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscorverserverApplication.class, args);
	}

}
