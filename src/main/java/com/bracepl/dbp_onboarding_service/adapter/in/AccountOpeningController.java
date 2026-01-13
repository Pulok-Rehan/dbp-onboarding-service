package com.bracepl.dbp_onboarding_service.adapter.in;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.AccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.models.EditAccountRequest;
import com.bracepl.dbp_onboarding_service.application.dtos.*;
import com.bracepl.dbp_onboarding_service.application.interfaces.AccountOpenUseCase;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    @PostMapping(value = "/account-open-photo-validation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResponse openAccountWithEkyc(@ModelAttribute RegisterDto registerDto,
                                               @RequestParam MultipartFile photo,
                                               @RequestParam MultipartFile photoTiltingLeft,
                                               @RequestParam MultipartFile photoTiltingRight,
                                               @RequestParam MultipartFile photoSmiling,
                                               @RequestParam MultipartFile photoBlinking) throws IOException {
        log.info("ENTERED INTO THE CONTROLLER FOR OPENING ACCOUNT EKYC...");
        return openAccountUseCase.openAccountWithEkyc(photo, photoTiltingLeft,photoTiltingRight,photoSmiling, photoBlinking, registerDto);
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
                                                    @RequestParam MultipartFile nidFront,
                                                    @RequestParam MultipartFile nidBack,
//                                                    @RequestParam MultipartFile tinCertificate,
                                                    @RequestParam(required = false) MultipartFile boAttachment,
                                                    @RequestParam(required = false) MultipartFile jointAccountPhoto,
                                                    @RequestParam(required = false) MultipartFile jointAccountSignature,
                                                    @RequestParam(required = false) MultipartFile jointAccountNidFront,
                                                    @RequestParam(required = false) MultipartFile jointAccountNidBack
                                                    )
            throws IOException {
        log.info("ENTERED INTO THE CONTROLLER FOR OPENING ACCOUNT DOCUMENTS...");
        return openAccountUseCase.openAccountWithDocuments(registerDto, nidFront, nidBack, nidBack, signature, chequeLeaf, boAttachment, jointAccountPhoto, jointAccountSignature, jointAccountNidFront, jointAccountNidBack);
    }

    @PostMapping(value = "/account-open-nominees", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResponse openAccountWithNominees(@ModelAttribute NomineeListDto nomineeListDto) throws IOException {
        log.info("ENTERED INTO THE CONTROLLER FOR OPENING ACCOUNT NOMINEES...");
        return openAccountUseCase.openAccountWithNomineeDetails(nomineeListDto);
    }

    @PostMapping(path = "/edit")
    public ServiceResponse editAccount(@RequestBody EditAccountRequest changeRequestDto) throws IOException {
        return openAccountUseCase.editAccount(changeRequestDto);
    }

    @GetMapping(path = "/search-information")
    public ServiceResponse searchAccountInfo(@RequestParam String input) throws JsonProcessingException {
        return openAccountUseCase.searchAccount(input);
    }

    @GetMapping(path = "/search")
    public AccountEntity searchAccount(@RequestParam String input) throws JsonProcessingException {
        return openAccountUseCase.getAccountSnapshot(input);
    }

    @GetMapping (path = "/getCompletionData")
    public ServiceResponse getCompletionData(@RequestParam String mobileNumber) throws JsonProcessingException {
        return openAccountUseCase.getCompletionData(mobileNumber);
    }

    @PostMapping (path = "/bo-payment")
    public ServiceResponse boPayment(@RequestParam String mobileNumber) throws JsonProcessingException {
        return openAccountUseCase.boPayment(mobileNumber);
    }

    @GetMapping(path = "/getEditRequest")
    public ServiceResponse getEditRequest(@RequestParam String mobileNumber) throws JsonProcessingException {
        return openAccountUseCase.searchAccount(mobileNumber);
    }

    @GetMapping(path = "/test")
    public String test() throws JsonProcessingException {
        return "CALLED...";
    }

}
