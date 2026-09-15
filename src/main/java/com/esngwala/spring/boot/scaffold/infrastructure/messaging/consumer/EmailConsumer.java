package com.esngwala.spring.boot.scaffold.infrastructure.messaging.consumer;

import com.esngwala.spring.boot.scaffold.infrastructure.config.properties.MailFromProperties;
import com.esngwala.spring.boot.scaffold.infrastructure.messaging.payload.EmailPayload;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Single consumer that handles all email queues.
 * Each @RabbitListener binding is resolved at runtime from application properties,
 * keeping the sending logic in one place and eliminating copy-paste duplication.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final JavaMailSender mailSender;
    private final MailFromProperties mailProps;

    @RabbitListener(queues = {
            "${rabbitmq.email.queues.welcome.name}",
            "${rabbitmq.email.queues.password-reset.name}",
            "${rabbitmq.email.queues.notification.name}"
    })
    public void handle(EmailPayload payload) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailProps.address(), mailProps.name());
            helper.setTo(payload.getTo());
            helper.setSubject(payload.getSubject());
            helper.setText(payload.getBody(), true); // HTML body

            mailSender.send(message);
            log.info("Email sent to {}: {}", payload.getTo(), payload.getSubject());

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Failed to send email to {}: {}", payload.getTo(), e.getMessage());
            // Rethrow as unchecked so the retry interceptor in SimpleRabbitListenerContainerFactory
            // can catch it, apply backoff, and eventually route to the dead-letter queue.
            throw new RuntimeException(e);
        }
    }
}
