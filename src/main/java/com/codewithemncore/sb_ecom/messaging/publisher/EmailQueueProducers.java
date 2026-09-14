package com.codewithemncore.sb_ecom.messaging.publisher;

import com.codewithemncore.sb_ecom.config.RabbitMQProperties;
import com.codewithemncore.sb_ecom.messaging.payload.EmailPayload;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class EmailQueueProducers {

    private final RabbitTemplate template;
    private final RabbitMQProperties props;

    public void sendWelcomeEmail(EmailPayload payload){
        template.convertAndSend(
                props.email().exchange(),
                props.email().queues().welcome().routingKey(),
                payload
        );
    }

    public void sendPasswordResetEmail(EmailPayload payload){
        template.convertAndSend(
                props.email().exchange(),
                props.email().queues().passwordReset().routingKey(),
                payload
        );
    }

    public void sendEmailNotification(EmailPayload payload){
        template.convertAndSend(
                props.email().exchange(),
                props.email().queues().notification().routingKey(),
                payload
        );
    }
}
