package com.bracepl.dbp_onboarding_service.domain.services;

import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.EkycService;
import com.bracepl.dbp_onboarding_service.application.dtos.NomineeDto;
import com.bracepl.dbp_onboarding_service.application.interfaces.NomineeUseCase;
import com.bracepl.dbp_onboarding_service.domain.interfaces.NomineeDomain;
import com.bracepl.dbp_onboarding_service.domain.models.Ekyc;
import com.bracepl.dbp_onboarding_service.domain.models.Nominee;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NomineeService implements NomineeUseCase {
    private final NomineeDomain nomineeDomain;
    private final EkycService ekycService;
    private final ObjectMapper objectMapper;

    public NomineeService(NomineeDomain nomineeDomain, EkycService ekycService, ObjectMapper objectMapper) {
        this.nomineeDomain = nomineeDomain;
        this.ekycService = ekycService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ServiceResponse addNominee(NomineeDto nomineeDto, MultipartFile nomineeNidFront, MultipartFile nomineeNidBack, String mobilenumber) throws IOException {
        List<Nominee> nominees = new ArrayList<>();
            log.info("CALLING EKYC SERVICE...");
            Ekyc ekyc = ekycService.callEkyc(nomineeNidFront, nomineeNidBack, mobilenumber);
            log.info("EKYC SERVICE RESPONSE: {}", ekyc);
            Nominee nominee = Nominee.builder()
                    .nomineeNidFront(nomineeNidFront.getBytes())
                    .nomineeNidBack(nomineeNidBack.getBytes())
                    .nomineeNidNumber(ekyc.getNid_no())
                    .nomineeDob(ekyc.getDate_of_birth())
                    .relation(nomineeDto.getRelation())
                    .nomineePercentage(nomineeDto.getNomineePercentage() == 0 ? 100 : nomineeDto.getNomineePercentage())
                    .name(nomineeDto.getName())
                    .build();
            log.info("ADDING NOMINEE...");
        String nomineeSaved = nomineeDomain.addNominee(nominee, mobilenumber);
        if (nomineeSaved.isEmpty()){
            return new ServiceResponse("Could not save nominee");
        }
        log.info("NOMINEE ADDED SUCCESSFULLY...");
        return new ServiceResponse("Nominee Saved", objectMapper.writeValueAsString(nominee));
    }

    @Override
    public ServiceResponse getNominee(String id) throws JsonProcessingException {
        log.info("GETTING NOMINEE...");
        Nominee nominee = nomineeDomain.getNominee(id);
        if (nominee == null){
            log.info("COULD NOT GET NOMINEE...");
            return new ServiceResponse("Could not find Nominee information");
        }
        log.info("NOMINEE FOUND: {}", nominee);
        return new ServiceResponse("Nominee found", objectMapper.writeValueAsString(nominee));
    }

    @Override
    public ServiceResponse getNominees(String mobileNumber) throws JsonProcessingException {
        log.info("GETTING NOMINEE...");
        List<Nominee> nomineeList = new ArrayList<>();
        nomineeList = nomineeDomain.getNominees(mobileNumber);
        if (nomineeList.isEmpty()){
            log.info("COULD NOT GET NOMINEE LIST...");
            return new ServiceResponse("Could not find Nominee information");
        }
        log.info("NOMINEES FOUND: {}", nomineeList);
        return new ServiceResponse("Nominee found", objectMapper.writeValueAsString(nomineeList));

    }
}
