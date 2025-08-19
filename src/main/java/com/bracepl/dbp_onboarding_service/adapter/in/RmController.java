package com.bracepl.dbp_onboarding_service.adapter.in;

import com.bracepl.dbp_onboarding_service.application.dtos.RmAction;
import com.bracepl.dbp_onboarding_service.application.interfaces.RmUseCase;
import com.bracepl.dbp_onboarding_service.domain.enums.Action;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/onboarding/rm")
public class RmController {
    private final RmUseCase rmUseCase;

    public RmController(RmUseCase rmUseCase) {
        this.rmUseCase = rmUseCase;
    }

    @GetMapping(path = "/getClients")
    public ServiceResponse getAccountOpeningRequests() throws JsonProcessingException {
        return rmUseCase.getClients();
    }
    @PostMapping(path = "/takeAction")
    public ServiceResponse getAccountOpeningRequest(@RequestBody RmAction rmAction) throws JsonProcessingException {
        return rmUseCase.takeActionOfClient(rmAction);
    }
    @GetMapping(path = "/contactClient")
    public ServiceResponse acceptAccount(@RequestParam String accountId) throws JsonProcessingException {
        return rmUseCase.contactClient(accountId);
    }


}
