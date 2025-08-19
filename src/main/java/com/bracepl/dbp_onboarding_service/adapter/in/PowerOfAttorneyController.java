package com.bracepl.dbp_onboarding_service.adapter.in;

import com.bracepl.dbp_onboarding_service.domain.interfaces.PowerOfAttorneyUseCase;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/onboarding/api")
public class PowerOfAttorneyController {
    private final PowerOfAttorneyUseCase powerOfAttorneyUseCase;

    public PowerOfAttorneyController(PowerOfAttorneyUseCase powerOfAttorneyUseCase) {
        this.powerOfAttorneyUseCase = powerOfAttorneyUseCase;
    }

    @PostMapping("/powerOfAttorney/grant")
    public ServiceResponse grantPowerOfAttorney(
            @RequestParam String mobileNumber,
            @RequestParam String requestId,
            @RequestParam String otp
    ) throws JsonProcessingException {
        return powerOfAttorneyUseCase.grantPowerOfAttorney(mobileNumber, requestId, otp);

    }

    @PostMapping("/powerOfAttorney/revoke")
    public ServiceResponse revokePowerOfAttorney(
            @RequestParam String mobileNumber,
            @RequestParam String requestId,
            @RequestParam String otp
    ) throws JsonProcessingException {
        return powerOfAttorneyUseCase.revokePowerOfAttorney(mobileNumber, requestId, otp);

    }
}
