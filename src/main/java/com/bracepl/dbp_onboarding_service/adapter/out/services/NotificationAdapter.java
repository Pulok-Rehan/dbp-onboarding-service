package com.bracepl.dbp_onboarding_service.adapter.out.services;
import com.bracepl.dbp_onboarding_service.domain.interfaces.NotificationDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class NotificationAdapter implements NotificationDomain {

    @Value("${notificationService.baseUrl}")
    private String baseUrl;
    @Value("${notificationService.sendOtp}")
    private String otpSend;
    @Value("${notificationService.otpValidate}")
    private String otpValidate;
    @Value("${notificationService.endpoint}")
    private String otpEndpoint;
    @Value("${notificationService.send-email}")
    private String sendEMail;
    @Value("${notificationService.send-sms}")
    private String sendSms;

    private final RestTemplate restTemplate;

    public NotificationAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String sendOtp(String senderEmail, String mobileNumber) {
        try {
            String url = baseUrl + otpSend + "?senderNumber={senderNumber}&senderEmail={senderEmail}";

            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("senderEmail", senderEmail);
            uriVariables.put("senderNumber", mobileNumber);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(new HttpHeaders()),
                    String.class,
                    uriVariables
            );

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Could not send OTP";
        }

    }

    @Override
    public boolean validateOtp(String mobileNumber, String otp) {
        try {
            String url = baseUrl + otpValidate + "?mobileNumber={mobileNumber}&otp={otp}";

            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("mobileNumber", mobileNumber);
            uriVariables.put("otp", otp);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(new HttpHeaders()),
                    String.class,
                    uriVariables
            );
            log.info("OTP VALIDATION RESPONSE: {}", response.getBody());
            return Objects.equals(response.getBody(), "true");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean findByEndPoint(String endPoint) {
        try {
            String url = baseUrl + otpEndpoint + "?endPoint={endPoint}";

            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("endPoint", endPoint);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(new HttpHeaders()),
                    String.class,
                    uriVariables
            );
//            log.info("IS ENDPOINT OTP ENABLED: {}", response.getBody());
            return !Objects.equals(response.getBody(), "false");
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }

    @Override
    public void sendEmail(String title, String body, String recipientEmail) {
        try {
            String url = baseUrl + sendEMail + "?title={title}&body={body}&recipientEmail={recipientEmail}";

            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("title", title);
            uriVariables.put("body", body);
            uriVariables.put("recipientEmail", recipientEmail);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(new HttpHeaders()),
                    String.class,
                    uriVariables
            );
            log.info("SEND EMAIL RESPONSE: {}", response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendSms(String title, String message, String recepientNumber) {
        try {
            String url = baseUrl + sendSms + "?title={title}&message={message}&recipientEmail={recipientEmail}";

            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("title", title);
            uriVariables.put("message", message);
            uriVariables.put("recipientEmail", recepientNumber);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(new HttpHeaders()),
                    String.class,
                    uriVariables
            );
            log.info("SEND SMS RESPONSE: {}", response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
