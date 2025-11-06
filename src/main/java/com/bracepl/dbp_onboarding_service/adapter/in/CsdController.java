package com.bracepl.dbp_onboarding_service.adapter.in;

import com.bracepl.dbp_onboarding_service.application.dtos.RemarkDto;
import com.bracepl.dbp_onboarding_service.application.interfaces.CsdUseCase;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/onboarding/csd")
public class CsdController {
    private final CsdUseCase csdUseCase;

    public CsdController(CsdUseCase csdUseCase) {
        this.csdUseCase = csdUseCase;
    }

    @PostMapping(path = "/addRemark")
    public ServiceResponse requestForChange(@RequestBody RemarkDto remarkDto){
        return csdUseCase.addRemark(remarkDto);
    }

    @GetMapping(path = "/getClientsFinal")
    public ServiceResponse getClientsFinal(@RequestParam String mobileNumber) throws JsonProcessingException {
        return csdUseCase.getClientsFinal(mobileNumber);
    }

    @GetMapping(path = "/getClientsInitiated")
    public ServiceResponse getClientsInitiated(@RequestParam String mobileNumber) throws JsonProcessingException {
        return csdUseCase.getClientsInitiated(mobileNumber);
    }

    @GetMapping(path = "/getClient")
    public ServiceResponse getClient(@RequestParam String accountId) throws JsonProcessingException {
        return csdUseCase.getCLient(accountId);
    }

    @GetMapping(path = "/getClients")
    public ServiceResponse getClients(@RequestParam String mobileNumber) throws JsonProcessingException {
        return csdUseCase.getClients(mobileNumber);
    }
}
