package com.bracepl.dbp_onboarding_service.domain.interfaces;


import com.bracepl.dbp_onboarding_service.adapter.out.entities.InternalUser;
import com.bracepl.dbp_onboarding_service.domain.models.AuthResponse;
import com.bracepl.dbp_onboarding_service.application.dtos.LoginDto;
import com.bracepl.dbp_onboarding_service.application.dtos.RegisterDto;
import com.bracepl.dbp_onboarding_service.domain.models.UserCredential;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;
import java.util.List;


public interface AuthDomain {
//    boolean savePasswordForNewUser();
//    void saveTokenForNewUser(PasswordResetToken passwordResetToken);
//    boolean savenewUserInCredTable(String mobileNumber, String password);
//    boolean updateUserInCredTable(String mobileNumber, String oldPassword, String newPassword);
    UserCredential findByEmail(String email);
//    PasswordResetToken changePassword(String email);
    UserCredential registerNewUser(String email, String mobileNo, String tempPassword, LocalDateTime expiryTime);
//    boolean generateNewTempPassword(Account account, String tempPassword, LocalDateTime expiryTime, String id);
    UserCredential findByMobileNumber(String mobileNumber);
    boolean updateTempPasswordStatus(String id) throws Exception;
    String getAdminAccessTokenFromKeycloak() throws JsonProcessingException;
    String findUserIdByUserNameFromKeycloak(String accessToken, String userName) throws JsonProcessingException;
    String findUserIdByEmailFromKeycloak(String accessToken, String email) throws JsonProcessingException;
    String registerUserInKeycloak(RegisterDto registerDto, String accessToken, String password, List<String> roles) throws JsonProcessingException;
    void assignRoleInKeycloak(String userId, String accessToken, List<String> role) throws JsonProcessingException;
    String resetPasswordInKeycloak(String password, String userId, boolean isFirstTime, String accessToken) throws JsonProcessingException;
    AuthResponse login(LoginDto loginDto);
    String setNewPassword(String userId, String newPassword, String accessToken);
    String forgotPassword(String userId, String password, String accessToken);
    InternalUser findByEmployeeCode(String employeeCode);
    boolean validateToken(String token);

}
