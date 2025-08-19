package com.bracepl.dbp_onboarding_service.adapter.in;

import com.bracepl.dbp_onboarding_service.application.dtos.NomineeDto;
import com.bracepl.dbp_onboarding_service.application.interfaces.NomineeUseCase;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/onboarding/api")
public class NomineeController {
    private final NomineeUseCase nomineeUseCase;

    public NomineeController(NomineeUseCase nomineeUseCase) {
        this.nomineeUseCase = nomineeUseCase;
    }

    @PostMapping(path = "/addNominee")
    public ServiceResponse addNominee(@ModelAttribute NomineeDto nomineeDtos,
                                      @RequestParam MultipartFile nomineeNidFront,
                                      @RequestParam MultipartFile nomineeNidBack,
                                      @RequestParam String mobileNumber) throws IOException {
        return nomineeUseCase.addNominee(nomineeDtos, nomineeNidFront, nomineeNidBack, mobileNumber);
    }

    @GetMapping(path = "/nominee")
    public ServiceResponse getNominee(@RequestParam String nomineeId) throws JsonProcessingException {
        return nomineeUseCase.getNominee(nomineeId);
    }

    @GetMapping(path = "/nominees")
    public ServiceResponse getNominees(@RequestParam String mobileNumber) throws JsonProcessingException {
        return nomineeUseCase.getNominees(mobileNumber);
    }
}
