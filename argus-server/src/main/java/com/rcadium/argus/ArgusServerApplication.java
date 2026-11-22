package com.rcadium.argus;

import com.rcadium.argus.config.TokenProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(TokenProperties.class)
public class ArgusServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArgusServerApplication.class, args);
	}

}
