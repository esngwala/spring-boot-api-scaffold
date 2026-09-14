package com.esngwala.spring.boot.scaffold;

import com.esngwala.spring.boot.scaffold.config.AppProperties;
import com.esngwala.spring.boot.scaffold.config.CorsProperties;
import com.esngwala.spring.boot.scaffold.config.MailFromProperties;
import com.esngwala.spring.boot.scaffold.config.RabbitMQProperties;
import com.esngwala.spring.boot.scaffold.config.RateLimitProperties;
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
