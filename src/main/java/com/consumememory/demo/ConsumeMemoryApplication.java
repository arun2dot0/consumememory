package com.consumememory.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class ConsumeMemoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumeMemoryApplication.class, args);
	}

}
