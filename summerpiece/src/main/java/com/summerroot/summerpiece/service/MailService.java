package com.summerroot.summerpiece.service;

import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.mail.javamail.JavaMailSender;
        import org.springframework.mail.javamail.MimeMessageHelper;
        import org.springframework.stereotype.Component;

        import javax.mail.MessagingException;
        import javax.mail.internet.MimeMessage;
        import java.util.HashMap;
        import java.util.Map;

@Component
public class MailService {

    @Autowired
    private JavaMailSender sender;

    public Map<String, Object> signUpEmailSend(String email, String subject, String body) {
        Map<String, Object> result = new HashMap<>();
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body);
            result.put("resultCode", 200); // success : OK, 요청 성공의 기본 상태 코드
        } catch (MessagingException e) {
            e.printStackTrace();
            result.put("resultCode", 500); // fail : (Internal Server Error) 서버에서 에러가 발생한 경우에 설정되는 기본 상태 코드
        }

        sender.send(message);
        return result;
    }
}
