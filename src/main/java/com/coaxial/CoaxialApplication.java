package com.coaxial;

import com.coaxial.config.DatabaseConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CoaxialApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CoaxialApplication.class);
        app.addInitializers(new DatabaseConfig());
        app.run(args);
    }

}
