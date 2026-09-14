package com.esngwala.spring.boot.scaffold.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQProperties props;

    //email queues, dead letter and routing

    @Bean
    public DirectExchange emailDeadLetterExchange(){
        return new DirectExchange(props.email().deadLetter().exchange());
    }

    @Bean
    public Queue emailDeadLetterQueue(){
        return QueueBuilder
                .durable(props.email().deadLetter().queue())
                .build();
    }

    @Bean
    public Binding emailDeadLetterBinding(){
        return BindingBuilder
                .bind(emailDeadLetterQueue())
                .to(emailDeadLetterExchange())
                .with(props.email().deadLetter().routingKey());
    }

    //Email exchange
    @Bean
    public DirectExchange emailExchange(){
        return  new DirectExchange(props.email().exchange());
    }

    //Email queues
    @Bean
    public Queue welcomeQueue(){
        return QueueBuilder
                .durable(props.email().queues().welcome().name())
                .withArgument("x-dead-letter-exchange", props.email().deadLetter().exchange())
                .withArgument("x-dead-letter-routing-key", props.email().deadLetter().routingKey())
                .build();
    }

    @Bean
    public Queue passwordQueue(){
        return QueueBuilder
                .durable(props.email().queues().passwordReset().name())
                .withArgument("x-dead-letter-exchange", props.email().deadLetter().exchange())
                .withArgument("x-dead-letter-routing-key", props.email().deadLetter().routingKey())
                .build();
    }

    @Bean
    public Queue emailNotificationQueue(){
        return QueueBuilder
                .durable(props.email().queues().notification().name())
                .withArgument("x-dead-letter-exchange", props.email().deadLetter().exchange())
                .withArgument("x-dead-letter-routing-key", props.email().deadLetter().routingKey())
                .build();
    }

    //Email queue bindings
    @Bean
    public Binding welcomeEmailBinding(){
        return BindingBuilder
                .bind(welcomeQueue())
                .to(emailExchange())
                .with(props.email().queues().welcome().routingKey());
    }

    @Bean
    public Binding passwordResetEmailBinding(){
        return BindingBuilder
                .bind(passwordQueue())
                .to(emailExchange())
                .with(props.email().queues().passwordReset().routingKey());
    }

    @Bean
    public Binding emailNotificationEmailBinding(){
        return BindingBuilder
                .bind(emailNotificationQueue())
                .to(emailExchange())
                .with(props.email().queues().notification().routingKey());
    }



    @Bean
    public MessageConverter converter(){
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setDefaultRequeueRejected(false);

        factory.setAdviceChain(
                RetryInterceptorBuilder
                        .stateless()
                        .maxRetries(3)
                        .backOffOptions(1000, 2.0, 10000)
                        .recoverer(new RejectAndDontRequeueRecoverer())
                        .build()
        );

        return factory;

    }

}
