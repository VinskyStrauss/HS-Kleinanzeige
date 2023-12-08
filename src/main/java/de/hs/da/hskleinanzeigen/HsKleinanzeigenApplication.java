package de.hs.da.hskleinanzeigen;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "de.hs.da.hskleinanzeigen")
@EntityScan("de.hs.da.hskleinanzeigen")
public class HsKleinanzeigenApplication {
    public static void main(String[] args) {
        SpringApplication.run(HsKleinanzeigenApplication.class, args);

    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("HS Kleinanzeigen")
                        .description("Plattform zum Verwalten von Kleinanzeigen"));
    }
}
