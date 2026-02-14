package com.example.ScopeIndiaProject.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class ContactMailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendContactMail(String to, String subject, String message) throws MessagingException {
        MimeMessage mimeMessage=mailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(mimeMessage,true);
        helper.setFrom("gopiikrisshnam@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(message,true);
        mailSender.send(mimeMessage);
    }
}
