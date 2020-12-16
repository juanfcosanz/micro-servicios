package com.papeleria.app.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
@EntityScan({"com.papeleria.app.commons.models.entity"})
public class MicroOauthApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroOauthApplication.class, args);
	}

}
