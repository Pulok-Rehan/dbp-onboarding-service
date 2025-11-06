package com.bracepl.dbp_onboarding_service.domain.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.InternalUser;
import com.bracepl.dbp_onboarding_service.application.dtos.*;
import com.bracepl.dbp_onboarding_service.domain.models.AuthResponse;
import com.bracepl.dbp_onboarding_service.application.interfaces.AuthUseCase;
import com.bracepl.dbp_onboarding_service.domain.interfaces.AuthDomain;
import com.bracepl.dbp_onboarding_service.domain.interfaces.NotificationDomain;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AuthService implements AuthUseCase {
    @Value("${email.regex}")
    private String emailRegex;
    @Value("${mobile.regex}")
    private String mobileRegex;

    private final AuthDomain authDomain;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectMapper objectMapper;
    private final NotificationDomain notificationDomain;

    public AuthService(AuthDomain authDomain, BCryptPasswordEncoder bCryptPasswordEncoder, ObjectMapper objectMapper, NotificationDomain notificationDomain) {
        this.authDomain = authDomain;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.objectMapper = objectMapper;
        this.notificationDomain = notificationDomain;
    }

    @Override
    public ServiceResponse requestNewPassword(String investorCode) {
        return null;
    }

    @Override
    public ServiceResponse login(LoginDto loginDto) throws Exception {
        String accessToken = authDomain.getAdminAccessTokenFromKeycloak();
        log.info("ACCESS TOKEN GENERATED...");
        String userId ;
        if (!Pattern.matches(emailRegex, loginDto.getUsername())){
            userId = authDomain.findUserIdByUserNameFromKeycloak(accessToken, loginDto.getUsername());
            log.info("USER ID FOUND USING MOBILE NUMBER...");
        }
        else {
            userId = authDomain.findUserIdByEmailFromKeycloak(accessToken, loginDto.getUsername());
            log.info("USER ID FOUND USING EMAIL ADDRESS...");
        }
        if (userId.isEmpty()) {
            log.info("COULD NOT FIND ANY ACCOUNT WITH THIS MOBILE: {}", loginDto.getUsername());
            return new ServiceResponse("COULD NOT FIND ANY ACCOUNT...");
        }
        AuthResponse authResponse = authDomain.login(loginDto);
        log.info("AUTH RESPONSE FOUND FROM SERVICE...");
        if (authResponse == null){
            return new ServiceResponse("Something went wrong please try again later.");
        }
        if (authResponse.getStatusCode().equals("401")){
            return new ServiceResponse("Invalid Credentials", objectMapper.writeValueAsString(authResponse));
        }
        if (authResponse.getStatusCode().equals("421")){
            return new ServiceResponse("Temporary Password needs to be changed", objectMapper.writeValueAsString(authResponse));
        }
        if (authResponse.getStatusCode().equals("423")){
            return new ServiceResponse("Your account is locked for 1 hour. Please try after 1 hour", objectMapper.writeValueAsString(authResponse));
        }
        return new ServiceResponse("Login Successful", objectMapper.writeValueAsString(authResponse));
    }

    @Override
    @Transactional
    public ServiceResponse register(RegisterDto registerDto, String otp) throws JsonProcessingException {
        String registeredUser;
        RegisterDto savedUser = RegisterDto.builder()
                .email(registerDto.getEmail())
                .mobileNumber(registerDto.getMobileNumber())
                .statusCode("421").build();
        String accessToken = authDomain.getAdminAccessTokenFromKeycloak();
        if (accessToken.isEmpty()) {
            log.info("COULD NOT GENERATE TOKEN FROM KEYCLOAK...");
            return new ServiceResponse("Could not communicate with authentication service");
        }
        log.info("ACCESS TOKEN GENERATED SUSSECCFULLY...");
        if (!Pattern.matches(emailRegex, registerDto.getEmail())) {
            log.info("INVALID EMAIL ADDRESS PROVIDED...");
            return new ServiceResponse("Please provide valid email.");
        }
        if (!Pattern.matches(mobileRegex, registerDto.getMobileNumber())) {
            log.info("INVALID MOBILE NUMBER PROVIDED...");
            return new ServiceResponse("Please provide valid mobile number.");
        }
        String userId = authDomain.findUserIdByUserNameFromKeycloak(accessToken, registerDto.getMobileNumber());
        if (!userId.isEmpty()) {
            log.info("ALREADY AN ACCOUNT WITH THIS MOBILE NUMBER OR EMAIL...");
            return new ServiceResponse("There is already an account with this email or mobileNumber");
        }
        if (otp == null || otp.isEmpty()) {
            log.info("SENDING OTP...");
            notificationDomain.sendOtp(registerDto.getEmail(), registerDto.getMobileNumber());
            log.info("OTP SENT TO MOBILE NUMBER: {}", registerDto.getMobileNumber());
            return new ServiceResponse("Otp Required", objectMapper.writeValueAsString(savedUser));
        }
        if (!notificationDomain.validateOtp(registerDto.getMobileNumber(), otp)) {
            log.info("OTP VALIDATION FAILED...");
            return new ServiceResponse("Otp did not match");
        }
        registeredUser = authDomain.registerUserInKeycloak(registerDto, accessToken, this.generateTempPassword(), new ArrayList<>());
        if (registeredUser.isEmpty()) {
            return new ServiceResponse("Could not register user. Please try again later");
        }
        log.info("KEYCLOAK USER REGISTRATION: {}", registeredUser);
        savedUser.setStatusCode("200");
        return new ServiceResponse("Temporary password is sent to your email and mobile number.", objectMapper.writeValueAsString(savedUser));
    }

    @Override
    @Transactional
    public ServiceResponse registerInternal(RegisterDtoInternal registerDtoInternal, String otp) throws JsonProcessingException {
        RegisterDto registerDto = new RegisterDto();
        String registeredUser;
        String accessToken = authDomain.getAdminAccessTokenFromKeycloak();
        if (accessToken.isEmpty()) {
            log.info("COULD NOT GENERATE TOKEN FROM KEYCLOAK...");
            return new ServiceResponse("Could not communicate with authentication service");
        }
        log.info("ACCESS TOKEN GENERATED SUSSECCFULLY...");
        InternalUser internalUser = authDomain.findByEmployeeCode(registerDtoInternal.getEmployeeCode());
        if (internalUser == null){
            log.info("COULD NOT FIND INTERNAL USER...");
            return new ServiceResponse(String.format("There is no employee with this employee code: %s", registerDtoInternal.getEmployeeCode()));
        }
        registerDto.setEmail(internalUser.getEmailAddress());
        registerDto.setMobileNumber(internalUser.getMobileNumber());
        String userId = authDomain.findUserIdByUserNameFromKeycloak(accessToken, internalUser.getMobileNumber());
        if (!userId.isEmpty()) {
            log.info("ALREADY AN ACCOUNT WITH THIS MOBILE NUMBER OR EMAIL...");
            return new ServiceResponse("There is already an account with this email or mobileNumber");
        }
        if (otp == null || otp.isEmpty()) {
            log.info("SENDING OTP...");
            notificationDomain.sendOtp(internalUser.getEmailAddress(), internalUser.getMobileNumber());
            log.info("OTP SENT TO MOBILE NUMBER: {}", internalUser.getMobileNumber());
            registerDto.setStatusCode("421");
            return new ServiceResponse("Otp Required", objectMapper.writeValueAsString(registerDto));
        }
        if (!notificationDomain.validateOtp(internalUser.getMobileNumber(), otp)) {
            log.info("OTP VALIDATION FAILED...");
            return new ServiceResponse("Otp did not match");
        }
        registeredUser = authDomain.registerUserInKeycloak(registerDto, accessToken, this.generateTempPassword(), internalUser.getRoles());
        if (registeredUser.isEmpty()) {
            return new ServiceResponse("Could not register user. Please try again later");
        }
        log.info("KEYCLOAK USER REGISTRATION: {}", registeredUser);
        registerDto.setStatusCode("200");
        return new ServiceResponse("Temporary password is sent to your email and mobile number.", objectMapper.writeValueAsString(registerDto));
    }

    @Override
    public ServiceResponse forgotPassword(ForgotPasswordDto forgotPasswordDto, String otp) throws JsonProcessingException {
        String accessToken = authDomain.getAdminAccessTokenFromKeycloak();
        RegisterDto authResponse = RegisterDto.builder()
                .statusCode("421")
                .mobileNumber(forgotPasswordDto.getMobileNumber())
                .email(forgotPasswordDto.getEmail()).build();
        log.info("KEYCLOAK ACCESS TOKEN GENERATED SUCCESSFULLY...");
        if (accessToken.isEmpty()){
            log.info("COULD NOT GENERATE TOKEN FROM KEYCLOAK...");
            return new ServiceResponse("Could not communicate with authentication service");
        }
        if (otp == null || otp.isEmpty()) {
            log.info("SENDING OTP...");
            notificationDomain.sendOtp(forgotPasswordDto.getEmail(), forgotPasswordDto.getMobileNumber());
            log.info("OTP SENT TO MOBILE NUMBER: {}", forgotPasswordDto.getMobileNumber());
            return new ServiceResponse("Otp Required", objectMapper.writeValueAsString(authResponse));
        }
        if (!notificationDomain.validateOtp(forgotPasswordDto.getMobileNumber(), otp)) {
            log.info("OTP VALIDATION FAILED...");
            return new ServiceResponse("Otp did not match");
        }
        String userId = authDomain.findUserIdByUserNameFromKeycloak(accessToken, forgotPasswordDto.getMobileNumber());
        if (userId.isEmpty()){
            return new ServiceResponse("There is no account with this email or mobileNumber");
        }
        String password = this.generateTempPassword();
        String temporaryPasswordSent = authDomain.forgotPassword(userId, password, accessToken);
        if (temporaryPasswordSent.isEmpty()){
            return new ServiceResponse("Could not register user. Please try again later");
        }
        log.info("Temporary password sent to : {}",forgotPasswordDto.getEmail());
        authResponse.setStatusCode("200");
        return new ServiceResponse("Temporary password sent to :" +  forgotPasswordDto.getEmail(), objectMapper.writeValueAsString(authResponse));
    }

    @Override
    public ServiceResponse setNewPassword(NewPasswordDto newPasswordDto) throws Exception {
        String accessToken = authDomain.getAdminAccessTokenFromKeycloak();
        log.info("KEYCLOAK TOKEN GENERATED SUCCESSFULLY...");
        String userId = authDomain.findUserIdByUserNameFromKeycloak(accessToken, newPasswordDto.getMobileNumber());
        if (userId.isEmpty()) {
            log.info("COULD NOT FIND ANY ACCOUNT WITH THIS MOBILE: {}", newPasswordDto.getMobileNumber());
            return new ServiceResponse("COULD NOT FIND ANY ACCOUNT...");
        }
        String passwordChanged = authDomain.setNewPassword(userId, newPasswordDto.getNewPassword(), accessToken);
        if (passwordChanged.isEmpty()){
            log.info("COULD NOT CHANGE PASSWORD FOR THIS MOBILE: {}", newPasswordDto.getMobileNumber());
            return new ServiceResponse("Could not reset password...");
        }
        log.info("NEW PASSWORD SET SUCCESSFUL...");
        return new ServiceResponse("Password Changed Successfully", passwordChanged);
    }

    private String generateTempPassword() {
        String password = UUID.randomUUID().toString().replaceAll("[^A-Za-z0-9]", "").substring(0, 6);
        log.info("PASSWORD IS: {}", password);
        return password;
    }
    private LocalDateTime setTempExpiryTime() {
        return LocalDateTime.now().plusSeconds(1800);
    }
}
