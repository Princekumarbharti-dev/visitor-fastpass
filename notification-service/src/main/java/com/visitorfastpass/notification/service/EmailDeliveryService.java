package com.visitorfastpass.notification.service;

import com.visitorfastpass.notification.config.MailDeliveryProperties;
import com.visitorfastpass.notification.domain.Notification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import java.time.format.DateTimeFormatter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class EmailDeliveryService {
    private final JavaMailSender mailSender;
    private final QrCodeService qrCodeService;
    private final MailDeliveryProperties properties;

    public EmailDeliveryService(JavaMailSender mailSender, QrCodeService qrCodeService,
                                MailDeliveryProperties properties) {
        this.mailSender = mailSender;
        this.qrCodeService = qrCodeService;
        this.properties = properties;
    }

    public boolean enabled() { return properties.enabled(); }

    public void sendPassEmail(Notification notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(properties.fromAddress());
            helper.setTo(notification.getRecipientEmail());
            helper.setSubject(notification.getSubject());
            helper.setText(html(notification), true);
            helper.addInline("visitorQr", new ByteArrayDataSource(
                    qrCodeService.png(notification.getDigitalPassUrl()), "image/png"));
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new IllegalStateException("Unable to create approval email", ex);
        }
    }

    private String html(Notification notification) {
        String name = escape(notification.getRecipientName());
        String host = escape(notification.getHostName());
        String purpose = escape(notification.getPurpose());
        String passNumber = escape(notification.getPassNumber());
        String url = escape(notification.getDigitalPassUrl());
        String schedule = escape(notification.getScheduledAt().format(DateTimeFormatter.RFC_1123_DATE_TIME));
        return """
                <!doctype html><html><body style="font-family:Arial,sans-serif;color:#1f2937">
                <div style="max-width:620px;margin:auto;border:1px solid #e5e7eb;border-radius:12px;padding:24px">
                  <h1 style="color:#1565c0">Your Visitor FastPass is approved</h1>
                  <p>Hello %s, your visit has been approved.</p>
                  <table style="width:100%%;line-height:1.8">
                    <tr><td><strong>Pass</strong></td><td>%s</td></tr>
                    <tr><td><strong>Host</strong></td><td>%s</td></tr>
                    <tr><td><strong>Purpose</strong></td><td>%s</td></tr>
                    <tr><td><strong>Visit time</strong></td><td>%s</td></tr>
                  </table>
                  <p style="text-align:center"><img src="cid:visitorQr" alt="Visitor QR pass" width="280" height="280"/></p>
                  <p style="text-align:center"><a href="%s" style="background:#1565c0;color:white;padding:12px 20px;text-decoration:none;border-radius:8px">View Digital Pass</a></p>
                  <p>Please present this QR code at reception. Do not share this email or pass link.</p>
                </div></body></html>
                """.formatted(name, passNumber, host, purpose, schedule, url);
    }

    private String escape(String value) { return HtmlUtils.htmlEscape(value == null ? "" : value); }
}
