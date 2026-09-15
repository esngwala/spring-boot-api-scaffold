package com.esngwala.spring.boot.scaffold.infrastructure.messaging.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "rabbitmq")
@Validated
public record RabbitMQProperties(
    @Valid Email email
) {
    ///email configurations from applocation.yml
    public record  Email(
            @NotBlank String exchange,
            @Valid Queues queues,
            @Valid DeadLetter deadLetter
    ){
        public record Queues(
            @Valid Welcome welcome,
            @Valid PasswordReset passwordReset,
            @Valid Notification notification
        ){
            public record Welcome(
                    @NotBlank String name,
                    @NotBlank String routingKey
            ){}
            public record PasswordReset(
                    @NotBlank String name,
                    @NotBlank String routingKey
            ){}
            public record Notification(
                    @NotBlank String name,
                    @NotBlank String routingKey
            ){}
        }

        public record DeadLetter(
                @NotBlank String exchange,
                @NotBlank String queue,
                @NotBlank String routingKey
        ){}
    }


}
