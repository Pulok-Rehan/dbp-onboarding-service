package com.bracepl.dbp_onboarding_service.adapter.in;

import com.bracepl.dbp_onboarding_service.adapter.out.models.AccountSearchRequest;
import com.bracepl.dbp_onboarding_service.application.interfaces.SettlementUseCase;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/onboarding/settlement")
public class SettlementController {
    private final SettlementUseCase settlementUseCase;

    public SettlementController(SettlementUseCase settlementUseCase) {
        this.settlementUseCase = settlementUseCase;
    }

    @GetMapping(path = "/getAllAccountOpeningRequests")
    public ServiceResponse getAccountOpeningRequests() throws JsonProcessingException {
        return settlementUseCase.getAccountOpeningRequests();
    }

    @GetMapping(path = "/getAccountOpeningRequest")
    public ServiceResponse getAccountOpeningRequest(@RequestParam String accountId) throws JsonProcessingException {
        return settlementUseCase.getAccountOpeningRequest(accountId);
    }

    @PostMapping(path = "/acceptAccount")
    public ServiceResponse acceptAccount(@RequestParam String accountId) throws JsonProcessingException {
        return settlementUseCase.acceptAccount(accountId);
    }

    @GetMapping(path = "/requestForChange")
    public ServiceResponse requestForChange(@RequestBody List<String> changeRequestList){
        return settlementUseCase.requsetForChange();
    }

    @PostMapping(path = "/search")
    public ServiceResponse searchAccount(@RequestBody AccountSearchRequest accountSearchRequest) throws JsonProcessingException {
        return settlementUseCase.searchAccount(accountSearchRequest);
    }

    @GetMapping(path = "/getCodeAndMobileNumberList")
    public ServiceResponse getCodeAndMobileNumberList() throws JsonProcessingException {
        return settlementUseCase.getCodeAndMobileNumberList();
    }
}
