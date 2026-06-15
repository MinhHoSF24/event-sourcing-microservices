package com.microservices.employeeservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Employee Api Specification - FullStack",
                description = "Api documentation for Employee Service",
                version = "1.0",
                contact = @Contact(
                        name = "Minh Ho",
                        email = "minhhonhat.sf.work@gmail.com",
                        url = ""
                ),
                license = @License(
                        name = "MIT License",
                        url = ""
                ),
                termsOfService = ""
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:9002"
                ),
                @Server(
                        description = "Dev ENV",
                        url = "https://employee-service.dev.com"
                ),
                @Server(
                        description = "Prod ENV",
                        url = "https://employee-service.prod.com"
                ),
        }
)
public class OpenApiConfig {
}