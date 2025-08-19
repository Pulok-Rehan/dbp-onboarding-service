package com.bracepl.dbp_onboarding_service.domain.interfaces;

public interface NotificationDomain {
    String sendOtp(String email, String mobileNumber);
    boolean validateOtp(String mobileNumber, String otp);
    boolean findByEndPoint(String endPoint);
    void sendEmail(String title, String emailBody, String recepientEmail);
    void sendSms(String title, String smsBody, String recepientNumber);
}
