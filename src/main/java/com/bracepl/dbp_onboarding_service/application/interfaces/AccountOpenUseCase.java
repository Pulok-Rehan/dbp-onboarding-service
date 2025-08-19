package com.bracepl.dbp_onboarding_service.application.interfaces;

import com.bracepl.dbp_onboarding_service.application.dtos.*;
import com.bracepl.dbp_onboarding_service.domain.models.PartialAccount;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AccountOpenUseCase {
    ServiceResponse openAccount(AccountOpeningDto dto, MultipartFile signature, MultipartFile chequeLeaf) throws IOException;
    ServiceResponse accountOpen(String partialAccountId) throws JsonProcessingException;
    ServiceResponse openAccountPartial(AccountOpeningDto dto, MultipartFile signature, MultipartFile chequeLeaf) throws IOException;
    ServiceResponse openAccountWithEkyc(MultipartFile nidFront, MultipartFile nidBack, MultipartFile photo, RegisterDto registerDto) throws IOException;
    ServiceResponse openAccountWithDocuments(RegisterDto registerDto, MultipartFile signature, MultipartFile chequeLeaf,MultipartFile boAttachment, MultipartFile jointAccountPicture,
                                             MultipartFile jointAccountSignature, MultipartFile jointNidFront,MultipartFile jointNidBack) throws IOException;
    ServiceResponse openAccountWithPersonalDetails(PersonalDetailsDto personalDetailsDto) throws JsonProcessingException;
    ServiceResponse openAccountWithAddress(AddressDto addressDto) throws JsonProcessingException;
    ServiceResponse openAccountWithBankDetails(BankDetailsDto bankDetailsDto) throws JsonProcessingException;
    ServiceResponse openAccountWithClientType(ClientTypeDto clientTypeDto) throws JsonProcessingException;
    ServiceResponse verifyNid(MultipartFile nidPhoto, MultipartFile photo) throws IOException;
    ServiceResponse extractNidData(MultipartFile nidFront, MultipartFile nidBack, MultipartFile photo, String investorCode, String boId) throws IOException;
    ServiceResponse verifyNidAstha(String nidNumber, String dateOfBirth, MultipartFile photo) throws IOException;
    ServiceResponse editAccount(PartialAccount partialAccount) throws JsonProcessingException;
    ServiceResponse searchAccount(String input) throws JsonProcessingException;
}
