package com.bracepl.dbp_onboarding_service.adapter.in;

import com.bracepl.dbp_onboarding_service.application.dtos.*;
import com.bracepl.dbp_onboarding_service.application.interfaces.AuthUseCase;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/onboarding/api")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping(value = "/public/login")
    public ServiceResponse openAccount(@RequestBody LoginDto loginDto) throws Exception {
        return authUseCase.login(loginDto);
    }

    @PostMapping(value = "/public/register")
    public ServiceResponse registerUser(@RequestBody RegisterDto registerDto, @RequestHeader String otp) throws IOException {
        return authUseCase.register(registerDto, otp);
    }

    @PostMapping(value = "/public/new-password")
    public ServiceResponse setNewPassword(@RequestBody NewPasswordDto newPasswordDto) throws Exception {
        return authUseCase.setNewPassword(newPasswordDto);
    }

    @PostMapping(value = "/public/forget-password")
    public ServiceResponse forgetPassword(@RequestBody ForgotPasswordDto forgotPasswordDto, @RequestHeader String otp) throws IOException {
        return authUseCase.forgotPassword(forgotPasswordDto, otp);
    }

    @PostMapping(value = "/reset-password")
    public ServiceResponse passwordReset(@RequestBody Map<String, String> investorCode) throws IOException {
        String investor = investorCode.get("investorCode");
        return authUseCase.requestNewPassword(investor);
    }

    @GetMapping(path = "/test")
    public String test() throws JsonProcessingException {
        return "Called";
    }

    @PostMapping(value = "/public/register/internal")
    public ServiceResponse registerUserInternal(@RequestBody RegisterDtoInternal registerDtoInternal) throws IOException {
        return authUseCase.registerInternal(registerDtoInternal);
    }

}
