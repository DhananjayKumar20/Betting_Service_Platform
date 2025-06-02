package com.BettingPlatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EntityScan(basePackages = "com.BettingPlatform.Model")
@EnableJpaRepositories(basePackages = "com.BettingPlatform.repository")
public class BettingServicePlatformsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BettingServicePlatformsApplication.class, args);
	}
	
	/*@Bean
	public RestTemplate restTemplate()
	{
		return new RestTemplate();

	}*/
	
}
