package com.summerroot.summerpiece.util;

import com.summerroot.summerpiece.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailUtils {

    private final JavaMailSender sender;

    public void sendEmail(Map<String, String> email) throws ServiceException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(email.get("address"));
            helper.setSubject(email.get("subject"));
            helper.setText(email.get("body"));

            sender.send(message);
        } catch (MessagingException e) {
            throw new ServiceException("메일 발송에 실패했습니다.");
        }
    }
}
