package com.exxeta.allocatorservicems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.exxeta")
@EnableFeignClients
public class AllocatorServiceMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AllocatorServiceMsApplication.class, args);
	}

}
