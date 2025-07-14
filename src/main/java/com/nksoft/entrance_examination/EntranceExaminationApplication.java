package com.nksoft.entrance_examination;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@OpenAPIDefinition(
		info = @Info(
				title = "Entrance Examination API",
				version = "1.0.0",
				description = "API documentation for the entrance examination system",
				license = @License(name = "Open Source License")))
@ConfigurationPropertiesScan("com.nksoft.entrance_examination.common.config.props")
@SpringBootApplication
public class EntranceExaminationApplication {
	public static void main(String[] args) {
		SpringApplication.run(EntranceExaminationApplication.class, args);
	}
}