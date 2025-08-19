package com.bracepl.dbp_onboarding_service.domain.services;

import com.bracepl.dbp_onboarding_service.domain.interfaces.NotificationDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
   private final NotificationDomain notificationDomain;

    public NotificationService(NotificationDomain notificationDomain) {
        this.notificationDomain = notificationDomain;
    }

    public String generateOtp(String email, String mobileNumber) {
        log.info("OTP INITIATED");
        return notificationDomain.sendOtp(email,mobileNumber);
    }

    public boolean validateOtp(String mobileNumber, String otp) {
        return notificationDomain.validateOtp(mobileNumber, otp);
    }

    public boolean findByEndpoint(String endpoint) {
        boolean otpEnabled = notificationDomain.findByEndPoint(endpoint);
        log.info("OTP ENABLED RESPONSE FOR THE ENDPOINT {} is {}", endpoint, otpEnabled);
        return otpEnabled;
    }

    public void sendEmail(String title, String emailBody, String recepientEmail){
        notificationDomain.sendEmail(title, emailBody, recepientEmail);
    }

    public void sendSms(String title, String smsBody, String recepientNumber){
        notificationDomain.sendSms(title, smsBody, recepientNumber);
    }
}
