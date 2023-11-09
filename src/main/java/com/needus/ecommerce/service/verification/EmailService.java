package com.needus.ecommerce.service.verification;

import com.needus.ecommerce.entity.user.ConfirmationToken;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.entity.user_order.UserOrder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
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
            context.setVariable("link","https://needus.store/activation?token="+token);
            String body = templateEngine.process("confirmation",context);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setTo(user.getEmail());
            helper.setSubject("Email Address Verification");
            helper.setText(body,true);
            javaMailSender.send(message);
        }
    }
    public void sendInvoiceMail(UserOrder order) throws MessagingException {
            Context context = new Context();
            context.setVariable("order",order);
            context.setVariable("address",order.getUserAddress());
            context.setVariable("orderItems",order.getOrderItems());
            context.setVariable("totalAmount",order.getTotalAmount());
            String body = templateEngine.process("orderInvoice",context);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setTo(order.getUserInformation().getEmail());
            helper.setSubject("Order Invoice");
            helper.setText(body,true);
            javaMailSender.send(message);
    }

}