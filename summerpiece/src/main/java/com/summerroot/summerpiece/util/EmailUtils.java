package com.summerroot.summerpiece.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Component
public class EmailUtils {

    @Autowired
    private JavaMailSender sender;

    public int sendEmail(String email, String subject, String body) {
        Map<String, Object> result = new HashMap<>();
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body);
            return 200;
        } catch (MessagingException e) {
            e.printStackTrace();
            return 500;
        }
    }
}
