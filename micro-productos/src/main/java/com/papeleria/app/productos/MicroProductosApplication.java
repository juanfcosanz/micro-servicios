package com.papeleria.app.productos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
@EntityScan({"com.papeleria.app.commons.models.entity"})
public class MicroProductosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroProductosApplication.class, args);
	}

}
