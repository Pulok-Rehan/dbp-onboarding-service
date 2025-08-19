package com.bracepl.dbp_onboarding_service.adapter.in;

import com.bracepl.dbp_onboarding_service.application.dtos.ActivateAccountDto;
import com.bracepl.dbp_onboarding_service.application.interfaces.ComplianceUseCase;
import com.bracepl.dbp_onboarding_service.application.interfaces.SettlementUseCase;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/onboarding/compliance")
public class ComplianceController {
    private final ComplianceUseCase complianceUseCase;

    public ComplianceController(ComplianceUseCase complianceUseCase) {
        this.complianceUseCase = complianceUseCase;
    }

    @PostMapping(path = "/activateAccount")
    public ServiceResponse acceptAccount(@RequestBody ActivateAccountDto activateAccountDto) throws JsonProcessingException {
        return complianceUseCase.activateAccount(activateAccountDto);
    }

    @GetMapping(path = "/getRms")
    public ServiceResponse getAlRms() throws JsonProcessingException {
        return complianceUseCase.getRms();
    }

    @PostMapping(path = "/instantActivation")
    public ServiceResponse acceptAccount(@RequestParam String accountId) throws JsonProcessingException {
        return complianceUseCase.instantBoActivation(accountId);
    }

    @GetMapping(path = "/getAcceptedAccounts")
    public ServiceResponse getAcceptedAccounts() throws JsonProcessingException {
        return complianceUseCase.getAcceptedAccounts();
    }
}
