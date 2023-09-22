package com.needus.ecommerce.service;

import com.needus.ecommerce.entity.ConfirmationToken;
import com.needus.ecommerce.entity.UserInformation;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service("emailService")
public class EmailService {

    private final TemplateEngine templateEngine;
    private  final ConfirmationTokenService confirmationTokenService;
    private  final JavaMailSender javaMailSender;


    @Autowired
    public EmailService(TemplateEngine templateEngine, ConfirmationTokenService confirmationTokenService, JavaMailSender javaMailSender) {
        this.templateEngine = templateEngine;
        this.confirmationTokenService = confirmationTokenService;
        this.javaMailSender = javaMailSender;
    }
    public void sendHtmlMail(UserInformation user) throws MessagingException {
        ConfirmationToken confirmationToken = confirmationTokenService.findByUser(user);
        if(confirmationToken!=null){
            String token = confirmationToken.getToken();
            Context context = new Context();
            context.setVariable("title","Verify Your Email Address");
            context.setVariable("link","http://localhost:8080/activation?token="+token);
            String body = templateEngine.process("confirmation",context);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setTo(user.getEmail());
            helper.setSubject("Email Address Verification");
            helper.setText(body,true);
            javaMailSender.send(message);
        }
    }

}