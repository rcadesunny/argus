package com.rcadium.argus;

import org.springframework.boot.SpringApplication;

public class TestArgusServerApplication {

	public static void main(String[] args) {
		SpringApplication.from(ArgusServerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
