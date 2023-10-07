//package com.needus.ecommerce.service.verification;
//
//import com.needus.ecommerce.config.TwilioConfig;
//import com.needus.ecommerce.model.OtpRequest;
//import com.needus.ecommerce.model.OtpResponseDto;
//import com.needus.ecommerce.model.enums.OtpStatus;
//import com.needus.ecommerce.model.OtpValidationRequest;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.text.DecimalFormat;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
//import java.util.Set;
//
//@Service
//@Slf4j
//public class SmsService {
//
//    Map<String, String> otpMap = new HashMap<>();
//    @Autowired
//    private TwilioConfig twilioConfig;
//
//    public void sendSMS(String phoneNumber) {
//        OtpResponseDto otpResponseDto = null;
//        try {
//            PhoneNumber to = new PhoneNumber(phoneNumber);//to
//            PhoneNumber from = new PhoneNumber(twilioConfig.getPhoneNumber()); // from
//            String otp = generateOTP();
//            String otpMessage = "Dear Customer , Your OTP is  " + otp + " for sending sms through Spring boot application. Thank You.";
//            Message message = Message
//                .creator(to, from,
//                    otpMessage)
//                .create();
//            otpMap.put(phoneNumber, otp);
//            log.info("OTP "+OtpStatus.DELIVERED.name());
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("OTP "+OtpStatus.FAILED.name());
//        }
//    }
//
//    public boolean validateOtp(String otpNumber) {
//        Set<String> keys = otpMap.keySet();
//        String phoneNumber = null;
//        for(String key : keys)
//            phoneNumber = key;
//        if (otpNumber.equals(username)) {
//            otpMap.remove(username,otpValidationRequest.getOtpNumber());
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    private String generateOTP() {
//        return new DecimalFormat("000000")
//            .format(new Random().nextInt(999999));
//    }
//
//}