package com.bracepl.dbp_onboarding_service.domain.services;

import com.bracepl.dbp_onboarding_service.application.dtos.RmAction;
import com.bracepl.dbp_onboarding_service.application.interfaces.RmUseCase;
import com.bracepl.dbp_onboarding_service.domain.enums.Action;
import com.bracepl.dbp_onboarding_service.domain.interfaces.RmDomain;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RmService implements RmUseCase {
    private final RmDomain rmDomain;
    private final ObjectMapper objectMapper;

    public RmService(RmDomain rmDomain, ObjectMapper objectMapper) {
        this.rmDomain = rmDomain;
        this.objectMapper = objectMapper;
    }

    @Override
    public ServiceResponse getClients() throws JsonProcessingException {
        List<Account> accountList = rmDomain.getAllClients("1");
        if (accountList.isEmpty()){
            log.info("COULD NOT GET ANY CLIENTS...");
            return new ServiceResponse("Could not get any client");
        }
        return new ServiceResponse("Clients retrieved successfully", objectMapper.writeValueAsString(accountList));
    }

    @Override
    public ServiceResponse takeActionOfClient(RmAction rmAction) throws JsonProcessingException {
        if (rmAction.getAction().equals(Action.REJECT)){
            if (rmAction.getReason().isEmpty()){
                log.info("REASON IS NOT PROVIDED FOR REJECTING THE CLIENT {}...", rmAction.getClientId());
                return new ServiceResponse("Please specify a reason");
            }
            boolean isRejected = rmDomain.rejectCLient(rmAction.getClientId(), rmAction.getReason());
            if (!isRejected){
                log.info("COULD NOT REJECT CLIENT...");
                return new ServiceResponse("Could not reject client");
            }
            return new ServiceResponse("Client rejected successfully", objectMapper.writeValueAsString(isRejected));
        }
        boolean isAccepted = rmDomain.acceptCLient(rmAction.getClientId());
        if (!isAccepted){
            log.info("COULD NOT ACCEPT CLIENT...");
            return new ServiceResponse("Could not accept client");
        }
        return new ServiceResponse("Client accepted successfully", objectMapper.writeValueAsString(isAccepted));
    }

    @Override
    public ServiceResponse contactClient(String accountId) throws JsonProcessingException {
        boolean isContacted = rmDomain.acceptCLient(accountId);
        if (!isContacted){
            log.info("COULD NOT CONTACT CLIENT...");
            return new ServiceResponse("Could not contact client");
        }
        return new ServiceResponse("Client contacted successfully", objectMapper.writeValueAsString(isContacted));
    }

    @Override
    public ServiceResponse addRemark(String remark, String accountId) {
        return null;
    }

    @Override
    public ServiceResponse getDashboard(String rmId) {
        return null;
    }
}
