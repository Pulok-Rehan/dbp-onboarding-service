package com.bracepl.dbp_onboarding_service.adapter.in;

import com.bracepl.dbp_onboarding_service.application.dtos.PowerOfAttorneyRequestDto;
import com.bracepl.dbp_onboarding_service.domain.interfaces.PowerOfAttorneyUseCase;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/onboarding/api")
public class PowerOfAttorneyController {
    private final PowerOfAttorneyUseCase powerOfAttorneyUseCase;

    public PowerOfAttorneyController(PowerOfAttorneyUseCase powerOfAttorneyUseCase) {
        this.powerOfAttorneyUseCase = powerOfAttorneyUseCase;
    }

    @PostMapping("/powerOfAttorney/grant")
    public ServiceResponse grantPowerOfAttorney(
            @RequestBody PowerOfAttorneyRequestDto powerOfAttorneyRequestDto,
            @RequestHeader String otp
    ) throws JsonProcessingException {
        return powerOfAttorneyUseCase.grantPowerOfAttorney(powerOfAttorneyRequestDto.getMobileNumber(), powerOfAttorneyRequestDto.getRequestId(), otp);

    }

    @PostMapping("/powerOfAttorney/revoke")
    public ServiceResponse revokePowerOfAttorney(
            @RequestBody PowerOfAttorneyRequestDto powerOfAttorneyRequestDto,
            @RequestHeader String otp
    ) throws JsonProcessingException {
        return powerOfAttorneyUseCase.revokePowerOfAttorney(powerOfAttorneyRequestDto.getMobileNumber(), powerOfAttorneyRequestDto.getRequestId(), otp);

    }

//    @GetMapping(path = "/investor")
//    public ServiceResponse getInvestor(@RequestParam Map<String, String> param) throws JsonProcessingException {
//        return powerOfAttorneyUseCase.getInvestor(param);
//    }
}
