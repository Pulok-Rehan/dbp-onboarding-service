package com.bracepl.dbp_onboarding_service.adapter.in;

import com.bracepl.dbp_onboarding_service.application.dtos.*;
import com.bracepl.dbp_onboarding_service.application.interfaces.AccountOpenUseCase;
import com.bracepl.dbp_onboarding_service.domain.models.PartialAccount;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/onboarding/api")
@Slf4j
public class AccountOpeningController {

    private final AccountOpenUseCase openAccountUseCase;

    @Autowired
    public AccountOpeningController(AccountOpenUseCase openAccountUseCase) {
        this.openAccountUseCase = openAccountUseCase;
    }

    @PostMapping(value = "/account-open", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResponse openAccount(@ModelAttribute AccountOpeningDto accountOpeningDto,
                                       @RequestParam MultipartFile signature,
                                       @RequestParam MultipartFile chequeLeaf) throws IOException {
        log.info("ENTERED INTO THE CONTROLLER FOR OPENING ACCOUNT...");
        return openAccountUseCase.openAccount(accountOpeningDto,signature, chequeLeaf);
    }

    @PostMapping(value = "/account-open-final")
    public ServiceResponse openAccountFinal(@RequestParam String partialAccountId) throws JsonProcessingException {
        log.info("ENTERED INTO THE CONTROLLER FOR OPENING ACCOUNT...");
        return openAccountUseCase.accountOpen(partialAccountId);
    }

    @PostMapping(value = "/account-open-ekyc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResponse openAccountWithEkyc(@ModelAttribute RegisterDto registerDto,
                                               @RequestParam MultipartFile nidFront,
                                               @RequestParam MultipartFile photo,
                                               @RequestParam MultipartFile nidBack) throws IOException {
        log.info("ENTERED INTO THE CONTROLLER FOR OPENING ACCOUNT EKYC...");
        return openAccountUseCase.openAccountWithEkyc(nidFront, nidBack, photo, registerDto);
    }

    @PostMapping(value = "/account-open-personal-details")
    public ServiceResponse openAccountWithPersonalDetails(@RequestBody PersonalDetailsDto personalDetailsDto) throws JsonProcessingException {
        log.info("ENTERED INTO THE CONTROLLER FOR OPENING ACCOUNT PERSONAL DETAILS...");
        return openAccountUseCase.openAccountWithPersonalDetails(personalDetailsDto);
    }

    @PostMapping(value = "/account-open-address")
    public ServiceResponse openAccountWithAddress(@RequestBody AddressDto addressDto) throws JsonProcessingException {
        log.info("ENTERED INTO THE CONTROLLER FOR OPENING ACCOUNT ADDRESS...");
        return openAccountUseCase.openAccountWithAddress(addressDto);
    }

    @PostMapping(value = "/account-open-bank-details")
    public ServiceResponse openAccountWithBankDetails(@RequestBody BankDetailsDto bankDetailsDto) throws IOException {
        log.info("ENTERED INTO THE CONTROLLER FOR OPENING ACCOUNT BANK DETAILS...");
        return openAccountUseCase.openAccountWithBankDetails(bankDetailsDto);
    }

    @PostMapping(value = "/account-open-client-type")
    public ServiceResponse openAccountWithClientType(@RequestBody ClientTypeDto clientTypeDto) throws IOException {
        log.info("ENTERED INTO THE CONTROLLER FOR OPENING ACCOUNT CLIENT TYPE...");
        return openAccountUseCase.openAccountWithClientType(clientTypeDto);
    }

    @PostMapping(value = "/account-open-documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResponse openAccountWithDocuments(@ModelAttribute RegisterDto registerDto,
                                                    @RequestParam MultipartFile signature,
                                                    @RequestParam MultipartFile chequeLeaf,
                                                    @RequestParam(required = false) MultipartFile boAttachment,
                                                    @RequestParam(required = false) MultipartFile jointAccountPhoto,
                                                    @RequestParam(required = false) MultipartFile jointAccountSignature,
                                                    @RequestParam(required = false) MultipartFile jointAccountNidFront,
                                                    @RequestParam(required = false) MultipartFile jointAccountNidBack
                                                    )
            throws IOException {
        log.info("ENTERED INTO THE CONTROLLER FOR OPENING ACCOUNT DOCUMENTS...");
        return openAccountUseCase.openAccountWithDocuments(registerDto, signature, chequeLeaf, boAttachment, jointAccountPhoto, jointAccountSignature, jointAccountNidFront, jointAccountNidBack);
    }

    @PostMapping(path = "/edit")
    public ServiceResponse editAccount(@RequestBody PartialAccount partialAccount) throws JsonProcessingException {
        return openAccountUseCase.editAccount(partialAccount);
    }

    @GetMapping(path = "/search")
    public ServiceResponse searchAccount(@RequestParam String input) throws JsonProcessingException {
        return openAccountUseCase.searchAccount(input);
    }

}
