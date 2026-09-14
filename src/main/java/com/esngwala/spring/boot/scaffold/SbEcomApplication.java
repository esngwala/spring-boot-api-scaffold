package com.esngwala.spring.boot.scaffold;

import com.esngwala.spring.boot.scaffold.infrastructure.config.AppProperties;
import com.esngwala.spring.boot.scaffold.infrastructure.config.CorsProperties;
import com.esngwala.spring.boot.scaffold.infrastructure.config.MailFromProperties;
import com.esngwala.spring.boot.scaffold.infrastructure.config.RabbitMQProperties;
import com.esngwala.spring.boot.scaffold.infrastructure.config.RateLimitProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({
        AppProperties.class,
        MailFromProperties.class,
        RabbitMQProperties.class,
        CorsProperties.class,
        RateLimitProperties.class
})
@EnableScheduling
public class SbEcomApplication {

    public static void main(String[] args) {
        SpringApplication.run(SbEcomApplication.class, args);
    }
}
