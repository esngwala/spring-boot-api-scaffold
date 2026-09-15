package com.esngwala.spring.boot.scaffold.application.service;

import com.esngwala.spring.boot.scaffold.infrastructure.config.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Year;

/**
 * Renders Thymeleaf email templates to HTML strings.
 * Templates live in resources/templates/email/.
 */
@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final TemplateEngine templateEngine;
    private final AppProperties appProps;

    /**
     * Renders the welcome email for a newly registered user.
     *
     * @param firstName the user's first name
     * @return rendered HTML string
     */
    public String renderWelcome(String firstName) {
        Context ctx = baseContext();
        ctx.setVariable("firstName", firstName);
        ctx.setVariable("loginUrl", appProps.baseUrl() + "/login");
        return templateEngine.process("email/welcome", ctx);
    }

    /**
     * Renders the password-reset email.
     *
     * @param firstName      the user's first name
     * @param email          the user's email address
     * @param rawToken       the raw (unhashed) reset token
     * @param expiryMinutes  how many minutes the link is valid
     * @return rendered HTML string
     */
    public String renderPasswordReset(String firstName, String email, String rawToken, long expiryMinutes) {
        Context ctx = baseContext();
        ctx.setVariable("firstName", firstName);
        ctx.setVariable("email", email);
        ctx.setVariable("resetUrl", appProps.baseUrl()
                + appProps.passwordReset().resetPath()
                + "?token=" + rawToken);
        ctx.setVariable("expiryMinutes", expiryMinutes);
        return templateEngine.process("email/password-reset", ctx);
    }

    /**
     * Renders a generic notification email.
     *
     * @param firstName the user's first name
     * @param subject   the notification subject (used as heading)
     * @param message   the HTML message body to inject into the template
     * @return rendered HTML string
     */
    public String renderNotification(String firstName, String subject, String message) {
        Context ctx = baseContext();
        ctx.setVariable("firstName", firstName);
        ctx.setVariable("subject", subject);
        ctx.setVariable("message", message);
        return templateEngine.process("email/notification", ctx);
    }

    /**
     * Renders the email address verification email.
     *
     * @param firstName    the user's first name
     * @param rawToken     the raw (unhashed) verification token
     * @param expiryHours  how many hours the link is valid
     * @return rendered HTML string
     */
    public String renderVerifyEmail(String firstName, String rawToken, long expiryHours) {
        Context ctx = baseContext();
        ctx.setVariable("firstName", firstName);
        ctx.setVariable("verifyUrl", appProps.baseUrl()
                + appProps.emailVerification().verifyPath()
                + "?token=" + rawToken);
        ctx.setVariable("expiryHours", expiryHours);
        return templateEngine.process("email/verify-email", ctx);
    }

    // --- helpers ---

    private Context baseContext() {
        Context ctx = new Context();
        ctx.setVariable("appName", appProps.name());
        ctx.setVariable("supportEmail", appProps.supportEmail());
        ctx.setVariable("year", Year.now().getValue());
        return ctx;
    }
}
