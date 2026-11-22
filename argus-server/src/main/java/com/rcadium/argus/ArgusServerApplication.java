package com.rcadium.argus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ArgusServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArgusServerApplication.class, args);
	}

}
