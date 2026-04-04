package com.zorvyn.finance.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Finance Dashboard API",
                version = "1.0",
                description = "Backend API for Finance Dashboard - " +
                        "Zorvyn Internship Assignment by MoulaBhanu Shaik"
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.APIKEY,
        paramName = "Authorization",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}