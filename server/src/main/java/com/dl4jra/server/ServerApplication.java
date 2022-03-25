package com.dl4jra.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(ServerApplication.class);

		builder.headless(false);

		ConfigurableApplicationContext context = builder.run(args);

//		SpringApplication.run(ServerApplication.class, args);


	}
}
