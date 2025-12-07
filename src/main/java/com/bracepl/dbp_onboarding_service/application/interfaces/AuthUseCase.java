package com.bracepl.dbp_onboarding_service.application.interfaces;


import com.bracepl.dbp_onboarding_service.application.dtos.*;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface AuthUseCase {
    ServiceResponse requestNewPassword(String investorCode);
    ServiceResponse login(LoginDto loginDto) throws Exception;
    ServiceResponse register(RegisterDto registerDto, String otp) throws JsonProcessingException;
    ServiceResponse registerInternal(RegisterDtoInternal registerDtoInternal, String otp) throws JsonProcessingException;
    ServiceResponse forgotPassword(ForgotPasswordDto forgotPasswordDto, String otp) throws JsonProcessingException;
    ServiceResponse setNewPassword(NewPasswordDto newPasswordDto) throws Exception;
    ServiceResponse validateToken(String token) throws Exception;
}
