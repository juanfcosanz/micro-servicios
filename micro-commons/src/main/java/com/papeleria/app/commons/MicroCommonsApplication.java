package com.papeleria.app.commons;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class MicroCommonsApplication {

	/*public static void main(String[] args) {
		SpringApplication.run(MicroCommonsApplication.class, args);
	}*/

}
