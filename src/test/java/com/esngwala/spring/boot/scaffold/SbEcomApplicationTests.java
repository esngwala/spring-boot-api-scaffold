package com.esngwala.spring.boot.scaffold;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:scaffold;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "spring.mail.host=localhost",
        "spring.mail.port=1025",
        "spring.mail.username=",
        "spring.mail.password=",
        "spring.rabbitmq.host=localhost",
        "spring.rabbitmq.port=5672",
        "spring.rabbitmq.username=guest",
        "spring.rabbitmq.password=guest",
        "spring.rabbitmq.listener.simple.auto-startup=false",
        "security.jwt.secret=VGhpcy1pc19hX3Rlc3Rfc2VjcmV0X2tleV90aGF0X2lzX2xvbmdfZW5vdWdoXzMyX2J5dGVz",
        "app.name=Scaffold Test",
        "app.support-email=support@example.test",
        "app.base-url=http://localhost",
        "app.cors.allowed-origins[0]=http://localhost:3000"
})
class SbEcomApplicationTests {

	@Test
	void contextLoads() {
	}

}
