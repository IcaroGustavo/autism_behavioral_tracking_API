package com.autismtracker.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("Autism Behavioral Tracking API")
				.description("MVP REST API for autism behavioral tracking and analysis")
				.version("v0.1.0")
				.license(new License().name("MIT")))
			.externalDocs(new ExternalDocumentation()
				.description("Swagger UI")
				.url("/swagger-ui.html"));
	}
}

