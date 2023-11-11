package com.needus.ecommerce.service.security;
import com.needus.ecommerce.exceptions.TechnicalIssueException;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class OtpService {

    @Value("${twilio.accountSid}")
    private String twilioAccountSid;

    @Value("${twilio.authToken}")
    private String twilioAuthToken;

    public String sendOtp(String phoneNumber) {
        try {
            String otp = String.format("%04d", new Random().nextInt(9999));
            Twilio.init(twilioAccountSid, twilioAuthToken);
            Message message = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber("+12293982399"),
                    "Your OTP is: " + otp)
                .create();

            log.info("OTP sent successfully: " + message.getSid());
            return otp;
        }
        catch (Exception e){
            log.error("Something went wrong while sending otp");
            throw new TechnicalIssueException("Something went wrong while sending otp");
        }
    }
}
