package edu.java.api.openapiConfiguration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
        title = "Telegram Bot Api",
        version = "1.0.0",
        contact = @Contact(
            name = "Evgenij Krupnov",
            url = "https://github.com/krupnoveo"
        )
    )
)
public class OpenApiConfig {
}
