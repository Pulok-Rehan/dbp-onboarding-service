package com.bracepl.dbp_onboarding_service.domain.services;

import com.bracepl.dbp_onboarding_service.adapter.out.models.NidVerificationResponse;
import com.bracepl.dbp_onboarding_service.application.dtos.*;
import com.bracepl.dbp_onboarding_service.config.SimpleMultipartFile;
import com.bracepl.dbp_onboarding_service.domain.enums.BoType;
import com.bracepl.dbp_onboarding_service.domain.models.*;
import com.bracepl.dbp_onboarding_service.domain.interfaces.AccountCompletionDomain;
import com.bracepl.dbp_onboarding_service.application.interfaces.AccountOpenUseCase;
import com.bracepl.dbp_onboarding_service.domain.interfaces.BankDomain;
import com.bracepl.dbp_onboarding_service.application.interfaces.PartialAccountDomain;
import com.bracepl.dbp_onboarding_service.domain.interfaces.AccountDomain;
import com.bracepl.dbp_onboarding_service.utils.ValidationUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AccountService implements AccountOpenUseCase {

    @Value("${nidverification.dir}")
    private String uploadDir;
    @Value("${email.regex}")
    private String emailRegex;
    @Value("${mobile.regex}")
    private String mobileRegex;
    @Value("${investor-code.dir}")
    private String investorCodeDirectory;
    @Value("${investor-code.prefix}")
    private String prefix;

    private static final DecimalFormat formatter = new DecimalFormat("00000");

    private final AccountDomain accountDomain;
    private final PartialAccountDomain partialAccountDomain;
    private final BankDomain bankDomain;
    private final ObjectMapper objectMapper;
    private final AccountCompletionDomain accountCompletionDomain;
    private final AuthService authService;

    public AccountService(AccountDomain accountDomain, PartialAccountDomain partialAccountDomain, BankDomain bankDomain, ObjectMapper objectMapper, AccountCompletionDomain accountCompletionDomain, AuthService authService) {
        this.accountDomain = accountDomain;
        this.partialAccountDomain = partialAccountDomain;
        this.bankDomain = bankDomain;
        this.objectMapper = objectMapper;
        this.accountCompletionDomain = accountCompletionDomain;
        this.authService = authService;
    }

    @Override
    public ServiceResponse openAccount(AccountOpeningDto accountOpeningDto, MultipartFile signature, MultipartFile chequeLeaf) throws IOException {
        List<MultipartFile> files = Arrays.asList(signature);
        String nidFront;
        String nidBack;
        String signatureUrl;
        String chequeLeafUrl;
        if (ValidationUtils.hasNullOrEmptyField(accountOpeningDto)) {
            log.info("NULL VALUE FOUND WHILE CHECKING NULL VALUE...");
            return new ServiceResponse("Null value found");
        }
        if (ValidationUtils.hasEmptyFile(files)) {
            log.info("NULL VALUE FOUND WHILE CHECKING NULL VALUE FOR FILES...");
            return new ServiceResponse("Empty file passed during account opening");
        }
        if (!Pattern.matches(emailRegex, accountOpeningDto.getEmail())){
            log.info("INVALID EMAIL PROVIDED");
            return new ServiceResponse("Invalid email provided");
        }
        if (!Pattern.matches(mobileRegex, accountOpeningDto.getMobileNumber())){
            log.info("INVALID MOBILE NUMBER PROVIDED");
            return new ServiceResponse("Invalid mobile number provided");
        }
        Account temporaryData = partialAccountDomain.findByMoBileNumber(accountOpeningDto.getMobileNumber());
        if (temporaryData == null){
            log.info("Could not get data");
            return new ServiceResponse("NID picture could not be found");
        }
        else {
            nidFront = temporaryData.getNidFront();
            nidBack = temporaryData.getNidBack();
            signatureUrl = this.convertToString(signature);
            chequeLeafUrl = this.convertToString(chequeLeaf);
        }
        if (accountDomain.isDuplicateAccount(accountOpeningDto.getNid())){
            log.info("DUPLICATE ACCOUNT FOUND WITH THIS NID {}", accountOpeningDto.getNid());
            return new ServiceResponse("ALREADY HAS AN ACCOUNT WITH THIS NID");
        }if (accountDomain.findByMobileNumber(accountOpeningDto.getMobileNumber())){
            log.info("DUPLICATE ACCOUNT FOUND WITH THIS MOBILE NUMBER {}", accountOpeningDto.getMobileNumber());
            return new ServiceResponse("ALREADY HAS AN ACCOUNT WITH THIS Mobile Number");
        }
        Account account = this.populateTOAccountObject(accountOpeningDto, signature, chequeLeaf);
        account = accountDomain.save(account);
        if (account == null){
            log.info("EXCEPTIION FOUND WHILE SAVING DATA...");
            return new ServiceResponse("Could not save account");
        }
        log.info("ACCOUNT SAVED IN THE DATABASE");
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(account.getMobileNumber(), account.getEmail(), true, true, true,true, false, true, true);
        if(completionSection == null){
            log.info("COULD NOT SAVE ACCOUNT COMPLETION DETAILS IN THE DATABASE...");
            return new ServiceResponse("Could not save Account Completion details");
        }
        else {
            account.setCompletionSection(completionSection);
        }

        return new ServiceResponse("Account saved successfully", objectMapper.writeValueAsString(account.getMobileNumber()));
    }

    @Override
    @Transactional
    public ServiceResponse accountOpen(String partialAccountId) throws JsonProcessingException {
        Account account;
        Account temporaryData = partialAccountDomain.findById(partialAccountId);
        if (temporaryData == null){
            log.info("Could not get data");
            return new ServiceResponse("Could not open BO account");
        }
        if (!Pattern.matches(emailRegex, temporaryData.getEmail())){
            log.info("INVALID EMAIL PROVIDED");
            return new ServiceResponse("Invalid email provided");
        }
        if (!Pattern.matches(mobileRegex, temporaryData.getMobileNumber())){
            log.info("INVALID MOBILE NUMBER PROVIDED");
            return new ServiceResponse("Invalid mobile number provided");
        }
        if (accountDomain.isDuplicateAccount(temporaryData.getNid())){
            log.info("DUPLICATE ACCOUNT FOUND WITH THIS NID {}", temporaryData.getNid());
            return new ServiceResponse("ALREADY HAS AN ACCOUNT WITH THIS NID");
        }if (!accountDomain.findByMobileNumber(temporaryData.getMobileNumber())){
            log.info("DUPLICATE ACCOUNT FOUND WITH THIS MOBILE NUMBER {}", temporaryData.getMobileNumber());
            return new ServiceResponse("ALREADY HAS AN ACCOUNT WITH THIS Mobile Number");
        }
        if (temporaryData.getJointAccountname() == null){
            account = accountDomain.save(temporaryData);
        }
        else if (temporaryData.isBoLinked()){
            account = accountDomain.saveWithBoLinked(temporaryData, temporaryData.getBoNumber());
        }
        else {
            account = accountDomain.saveWithJointAccount(temporaryData, this.populateToJointAccountModel(temporaryData));
        }
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://localhost:8082/api/investor/add?mobileNumber=" + temporaryData.getMobileNumber();
//        restTemplate.postForObject(url, null, String.class);
        log.info("ACCOUNT SAVED IN THE DATABASE");
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(temporaryData.getMobileNumber(), temporaryData.getEmail(), true, true, true,true, false, true, true);
        if(completionSection == null){
            log.info("COULD NOT SAVE ACCOUNT COMPLETION DETAILS IN THE DATABASE...");
            return new ServiceResponse("Could not save Account Completion details");
        }
        if (!this.makePartialAccountActive(temporaryData)){
            log.info("COULD NOT MAKE PARTIAL ACCOUNT ACTIVE...");
        }
        log.info("ACCOUNT SAVED: {}", temporaryData);



        return new ServiceResponse("Account saved successfully", objectMapper.writeValueAsString(temporaryData.getMobileNumber()));
    }

    @Override
    public ServiceResponse openAccountPartial(AccountOpeningDto accountOpeningDto, MultipartFile signature, MultipartFile chequeLeaf) throws IOException {
//        if (partialAccountDomain.findByMoBileNumberList(accountOpeningDto.getMobileNumber())){
//            log.info("MULTIPLE ACCOUNT WITH THIS MOBILE NUMBER: {}", accountOpeningDto.getMobileNumber());
//            return new ServiceResponse("There is already an account with this mobile number: {}", accountOpeningDto.getMobileNumber());
//        }
        Account savedAccount = partialAccountDomain.findByMoBileNumber(accountOpeningDto.getMobileNumber());
        String accountId = null;
        String nidFront = null;
        String nidBack = null;
        if (savedAccount != null){
            accountId = savedAccount.getId();
            nidFront = savedAccount.getNidFront();
            nidBack = savedAccount.getNidBack();
        }
        Account account = this.populateToPartialAccountObject(accountOpeningDto, signature, accountId, nidFront, nidBack, chequeLeaf);
        account = partialAccountDomain.save(account, false);
        if (account == null){
            log.info("EXCEPTIION FOUND WHILE SAVING DATA...");
            return new ServiceResponse("Could not save account");
        }
        log.info("ACCOUNT SAVED IN THE DATABASE");

        return new ServiceResponse("Parial Account information saved successfully", objectMapper.writeValueAsString(account));
    }

    @Override
    public ServiceResponse openAccountWithEkyc(MultipartFile nidFront, MultipartFile nidBack, MultipartFile photo, RegisterDto registerDto) throws IOException {
        if (registerDto.getMobileNumber() == null || registerDto.getEmail() == null){
            log.info("NULL EMAIL OR MOBILE NUMBER PROVIDED");
            return new ServiceResponse("Mobile number or Email can not be empty.");
        }
        Account temporaryData = partialAccountDomain.findByMoBileNumber(registerDto.getMobileNumber());
        if (temporaryData != null && temporaryData.isActive()){
            log.info("THERE IS ALREADY AN SAVED ACCOUNT WITH THIS MOBILE NUMBER: {}", registerDto.getMobileNumber());
            return new ServiceResponse("You can not use this mobile number. Please use a different number.");
        }
        List<MultipartFile> files = Arrays.asList(nidFront, nidBack, photo);
        if (ValidationUtils.hasEmptyFile(files)) {
            log.info("NULL FILE FOUND WHILE ACCOUNT OPENING WITH KYC...");
            return new ServiceResponse("Null value found");
        }
        Ekyc ekyc = accountDomain.callEkycService(nidFront, nidBack, photo, registerDto.getMobileNumber());
        if (ekyc == null){
            log.info("FOUND NULL RESPONSE FROM KYC SERVICE...");
            return new ServiceResponse("Could not get information from ekyc");
        }
        log.info("RESPONSE FROM KYC SERVER: {}", ekyc);
//        if (!ekyc.isMatched()){
//            return new ServiceResponse("Photo did not match with your NID.");
//        }
//        NidVerificationResponse nidVerificationResponse = accountDomain.callNidVerification(ekyc.getNid_no(), ekyc.getDate_of_birth(), photo, nidFront, "CLIENT_PORTAL");
//        if (nidVerificationResponse == null){
//            log.info("COULD NOT CALL API FOR NID VERIFICATION");
//            return new ServiceResponse("NID verification failed");
//        }
//        double faceSimilarity = Double.parseDouble(nidVerificationResponse.getFaceSimilarity().replaceAll("%", ""));
//        if (faceSimilarity < 50.0){
//            log.info("FACE SIMILARITY IS LESS THAN 50%");
//            return new ServiceResponse("Face is not similar with NID");
//        }
        Account account = Account.builder()
                .id(temporaryData == null ? null : temporaryData.getId())
                .nidFront(this.convertToString(nidFront))
                .nidBack(this.convertToString(nidBack))
                .photo(this.convertToString(photo))
                .mobileNumber(registerDto.getMobileNumber())
                .email(registerDto.getEmail())
                .dateOfBirth(ekyc.getDate_of_birth())
                .name(ekyc.getName())
                .nid(ekyc.getNid_no())
                .build();
        Account savedAccount = partialAccountDomain.save(account, false);
        if (savedAccount == null){
            log.info("COULD NOT SAVE PARTIAL DATA...");
            return new ServiceResponse("Could not save partial data");
        }
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(savedAccount.getMobileNumber(), savedAccount.getEmail(), true, false, false,false,false,false, false);
        if(completionSection == null){
            log.info("COULD NOT SAVE ACCOUNT COMPLETION DETAILS IN THE DATABASE...");
            return new ServiceResponse("Could not save Account Completion details");
        }
        else {
            account.setCompletionSection(completionSection);
        }

        return new ServiceResponse("Information retrived successfully", objectMapper.writeValueAsString(savedAccount));
    }

    @Override
    public ServiceResponse openAccountWithDocuments(RegisterDto registerDto, MultipartFile signature, MultipartFile chequeLeaf, MultipartFile boAttachment, MultipartFile jointAccountPicture, MultipartFile jointAccountSignature, MultipartFile jointNidFront, MultipartFile jointNidBack) throws IOException {
        Account temporaryData = partialAccountDomain.findByMoBileNumber(registerDto.getMobileNumber());
        if (temporaryData == null){
            log.info("THERE IS NO SAVED ACCOUNT WITH THIS MOBILE NUMBER: {}", registerDto.getMobileNumber());
            return new ServiceResponse("Could not save user data");
        }
        List<MultipartFile> files = Arrays.asList(signature, chequeLeaf);
        if (ValidationUtils.hasEmptyFile(files)) {
            log.info("NULL FILE FOUND WHILE ACCOUNT OPENING WITH DOCUMENTS UPLOAD...");
            return new ServiceResponse("Null value found");
        }
        if (temporaryData.getBoType().equals(BoType.JOINT.toString())){
            temporaryData.setJointAccountSignature(this.convertToString(jointAccountSignature));
            temporaryData.setJointAccountPhoto(this.convertToString(jointAccountPicture));
            temporaryData.setJointAccountNidFront(this.convertToString(jointNidFront));
            temporaryData.setJointAccountNidBack(this.convertToString(jointNidBack));
        }
        temporaryData.setSignature(this.convertToString(signature));
        temporaryData.setChequeLeaf(this.convertToString(chequeLeaf));
        Account savedAccount = partialAccountDomain.save(temporaryData, false);
        if (savedAccount == null){
            log.info("COULD NOT SAVE PARTIAL DATA...");
            return new ServiceResponse("Could not save partial data");
        }
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(temporaryData.getMobileNumber(), temporaryData.getEmail(), true, true, true,true,false,true, false);
        if(completionSection == null){
            log.info("COULD NOT SAVE ACCOUNT COMPLETION DETAILS IN THE DATABASE...");
            return new ServiceResponse("Could not save Account Completion details");
        }
        else {
            log.info("PARTIAL ACCOUNT SAVED: {}", savedAccount);
            return new ServiceResponse("Information retrived successfully", objectMapper.writeValueAsString(savedAccount));
        }
    }

    @Override
    public ServiceResponse openAccountWithPersonalDetails(PersonalDetailsDto personalDetailsDto) throws JsonProcessingException {
        if (ValidationUtils.hasNullOrEmptyField(personalDetailsDto)) {
            log.info("NULL VALUE FOUND WHILE CHECKING NULL VALUE...");
            return new ServiceResponse("Null value found");
        }
        Account temporaryData = partialAccountDomain.findByNid(personalDetailsDto.getNid());
        if (temporaryData != null) {
            log.info("COULD NOT SAVE ACCOOUNT INFORMATION WITH THIS NID: {}", personalDetailsDto.getNid());
            return new ServiceResponse("There is already an account with this NID");
        }
        temporaryData = partialAccountDomain.findByMoBileNumber(personalDetailsDto.getMobileNumber());
        if (temporaryData == null) {
            log.info("COULD NOT SAVE ACCOOUNT INFORMATION WITH THIS MOBILE NUMBER: {}", personalDetailsDto.getMobileNumber());
            return new ServiceResponse("Could not save account information");
        }
        temporaryData.setName(personalDetailsDto.getName());
        temporaryData.setNid(personalDetailsDto.getNid());
        temporaryData.setGender(personalDetailsDto.getGender());
        temporaryData.setEmail(personalDetailsDto.getEmail());
        temporaryData.setMobileNumber(personalDetailsDto.getMobileNumber());
        temporaryData.setFathersName(personalDetailsDto.getFathersName());
        temporaryData.setMothersName(personalDetailsDto.getMothersName());
        temporaryData.setDateOfBirth(personalDetailsDto.getDateOfBirth());
        temporaryData.setResidency(personalDetailsDto.getResidency());
        Account savedAccount = partialAccountDomain.save(temporaryData, false);
        if (savedAccount == null) {
            log.info("COULD NOT SAVE PARTIAL DATA...");
            return new ServiceResponse("Could not save partial data");
        }
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(temporaryData.getMobileNumber(), temporaryData.getEmail(), true, true, false, false, false, false, false);
        if (completionSection == null) {
            log.info("COULD NOT SAVE ACCOUNT COMPLETION DETAILS IN THE DATABASE...");
            return new ServiceResponse("Could not save Account Completion details");
        }
        log.info("PARTIAL ACCOUNT SAVED: {}", savedAccount);
        return new ServiceResponse("Information retrived successfully", objectMapper.writeValueAsString(savedAccount));
    }

    @Override
    public ServiceResponse openAccountWithAddress(AddressDto addressDto) throws JsonProcessingException {
        if (ValidationUtils.hasNullOrEmptyField(addressDto)) {
            log.info("NULL VALUE FOUND WHILE CHECKING NULL VALUE...");
            return new ServiceResponse("Null value found");
        }
        Account temporaryData = partialAccountDomain.findByMoBileNumber(addressDto.getMobileNumber());
        if (temporaryData == null) {
            log.info("COULD NOT SAVE ACCOOUNT INFORMATION WITH THIS MOBILE NUMBER: {}", addressDto.getMobileNumber());
            return new ServiceResponse("Could not save account information");
        }
                temporaryData.setAddressLine1(addressDto.getAddressLine1());
                temporaryData.setCity(addressDto.getCity());
                temporaryData.setCountry(addressDto.getCountry());
                temporaryData.setState(addressDto.getState());
                temporaryData.setZipCode(addressDto.getZipCode());
        Account savedAccount = partialAccountDomain.save(temporaryData, false);
        if (savedAccount == null){
            log.info("COULD NOT SAVE PARTIAL DATA...");
            return new ServiceResponse("Could not save partial data");
        }
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(temporaryData.getMobileNumber(), temporaryData.getEmail(),
                true, true, true
                ,false,false,false, false);
        if(completionSection == null){
            log.info("COULD NOT SAVE ACCOUNT COMPLETION DETAILS IN THE DATABASE...");
            return new ServiceResponse("Could not save Account Completion details");
        }
        log.info("PARTIAL ACCOUNT SAVED: {}", savedAccount);
        return new ServiceResponse("Information retrived successfully", objectMapper.writeValueAsString(savedAccount));
    }

    @Override
    public ServiceResponse openAccountWithBankDetails(BankDetailsDto bankDetailsDto) throws JsonProcessingException {
        Account savedAccount;
        if (bankDetailsDto.getBoType().equals("JOINT") && bankDetailsDto.isBoLinked()) {
            if (ValidationUtils.hasNullOrEmptyField(bankDetailsDto)) {
                log.info("NULL VALUE FOUND WHILE CHECKING NULL VALUE...");
                return new ServiceResponse("Null value found");
            }
        }
        Account temporaryData = partialAccountDomain.findByMoBileNumber(bankDetailsDto.getMobileNumber());
        if (temporaryData == null) {
            log.info("COULD NOT SAVE ACCOOUNT INFORMATION WITH THIS MOBILE NUMBER: {}", bankDetailsDto.getMobileNumber());
            return new ServiceResponse("Could not save account information");
        }
        temporaryData.setBoType(bankDetailsDto.getBoType());
        temporaryData.setBankName(bankDetailsDto.getBankName());
        temporaryData.setBranchName(bankDetailsDto.getBranchName());
        temporaryData.setRoutingNumber(bankDetailsDto.getRoutingNumber());
        temporaryData.setAccountNo(bankDetailsDto.getAccountNo());
        temporaryData.setBoLinked(bankDetailsDto.isBoLinked());
        if (bankDetailsDto.isBoLinked()) {
            temporaryData.setBoNumber(bankDetailsDto.getBoNumber());
        }
        if (bankDetailsDto.getBoType().equals(BoType.JOINT.toString())) {
            temporaryData.setJointAccountname(bankDetailsDto.getJointAccountName());
            temporaryData.setJointAccountAddress(bankDetailsDto.getJointAccountAddress());
            temporaryData.setJointAccountEmail(bankDetailsDto.getJointAccountEmail());
            savedAccount = partialAccountDomain.saveWithJointAccount(temporaryData, false);
        }
        else {
            savedAccount = partialAccountDomain.save(temporaryData, false);
        }
        if (savedAccount == null) {
            log.info("COULD NOT SAVE PARTIAL DATA...");
            return new ServiceResponse("Could not save partial data");
        }
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(temporaryData.getMobileNumber(), temporaryData.getEmail(), true, true, true, true, false, false, false);
        if (completionSection == null) {
            log.info("COULD NOT SAVE ACCOUNT COMPLETION DETAILS IN THE DATABASE...");
            return new ServiceResponse("Could not save Account Completion details");
        }
        log.info("PARTIAL ACCOUNT SAVED: {}", savedAccount);
        return new ServiceResponse("Information retrived successfully", objectMapper.writeValueAsString(savedAccount));
    }

    @Override
    public ServiceResponse openAccountWithClientType(ClientTypeDto clientTypeDto) throws JsonProcessingException {
        if (ValidationUtils.hasNullOrEmptyField(clientTypeDto)) {
            log.info("NULL VALUE FOUND WHILE CHECKING NULL VALUE...");
            return new ServiceResponse("Null value found");
        }

        Account temporaryData = partialAccountDomain.findByMoBileNumber(clientTypeDto.getMobileNumber());
        if (temporaryData == null) {
            log.info("COULD NOT SAVE ACCOOUNT INFORMATION WITH THIS MOBILE NUMBER: {}", clientTypeDto.getMobileNumber());
            return new ServiceResponse("Could not save account information");
        }
        if (clientTypeDto.isBoLinked()){
            temporaryData.setBoNumber(clientTypeDto.getBoNumber());
        }
        if (clientTypeDto.getBoType().equals(BoType.JOINT.toString())){
            temporaryData.setJointAccountname(clientTypeDto.getName());
            temporaryData.setJointAccountAddress(clientTypeDto.getAddress());
            temporaryData.setJointAccountEmail(clientTypeDto.getEmail());
        }
        Account savedAccount = partialAccountDomain.saveClientType(temporaryData);
        if (savedAccount == null){
            log.info("COULD NOT SAVE PARTIAL DATA...");
            return new ServiceResponse("Could not save partial data");
        }
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(savedAccount.getMobileNumber(), savedAccount.getEmail(), true, true, true,true,false,false, false);
        if(completionSection == null){
            log.info("COULD NOT SAVE ACCOUNT COMPLETION DETAILS IN THE DATABASE...");
            return new ServiceResponse("Could not save Account Completion details");
        }
        return new ServiceResponse("Information retrived successfully", objectMapper.writeValueAsString(savedAccount));
    }

    @Override
    public ServiceResponse verifyNid(MultipartFile nidPhoto, MultipartFile photo) throws IOException {
        log.info("ENTERED SERVICE LAYER FOR EXTRACTING DATA...");
        ClassPathResource resource = new ClassPathResource("nidBack.jpg");
        this.convertToString(nidPhoto);
        byte[] fileBytes;
        EkycResponse ekycResponse = EkycResponse.builder()
                .code(403).build();

        try (InputStream inputStream = resource.getInputStream()) {
            fileBytes = inputStream.readAllBytes();
        }

        MultipartFile nidBack = new SimpleMultipartFile(
                "nidBack",
                resource.getFilename(),
                "image/jpeg",
                fileBytes
        );
        Ekyc ekyc = accountDomain.callEkycService(nidPhoto, nidBack, photo, "ASTHA_USER");
        if (ekyc == null){
            log.info("FOUND NULL RESPONSE FROM KYC SERVICE...");
            ekycResponse.setStatus("Couldn’t call NID data extraction service");
            ekycResponse.setCode(428);
            return new ServiceResponse("Full verification needed", objectMapper.writeValueAsString(ekycResponse), true);
        }
        if (ekyc.getNid_no() == null || ekyc.getDate_of_birth() == null){
            log.info("NID: {} DOB:{}", ekyc.getNid_no(), ekyc.getDate_of_birth());
            ekycResponse.setCode(428);
            ekycResponse.setStatus("NID Data Extraction Service couldn’t extract data");
            return new ServiceResponse("Full verification needed",objectMapper.writeValueAsString(ekycResponse), false);
        }
//        return new ServiceResponse("Data extraction successfull", objectMapper.writeValueAsString(ekyc), false);
        log.info("RESPONSE FROM KYC SERVER: {}", ekyc);
//        NidVerificationResponse nidVerificationResponse = accountDomain.callNidVerification(ekyc.getNid_no(), ekyc.getDate_of_birth(), photo, nidPhoto, "ASTHA");
        NidVerificationResponse nidVerificationResponse = this.demoResponseFromRVL();
        if (nidVerificationResponse == null){
            log.info("COULD NOT CALL API FOR NID VERIFICATION");
            ekycResponse.setStatus("Couldn’t Call RVL");
            ekycResponse.setCode(428);
            return new ServiceResponse("Full Verification needed", objectMapper.writeValueAsString(ekycResponse), true);
        }
        if (nidVerificationResponse.getCount() == 1) {
            log.info("API CALLED 1 TIMES ALREADY");
            ekycResponse.setCode(400);
            ekycResponse.setStatus("Called RVL with NID no: " + ekyc.getNid_no());
            return new ServiceResponse("Full verification needed", objectMapper.writeValueAsString(ekycResponse), false);

        }
        if (!nidVerificationResponse.isSuccess()){
            log.info("DOB: {} NID: {}", ekyc.getDate_of_birth(), ekyc.getNid_no());
            ekycResponse.setCode(400);
            ekycResponse.setFaceSimilarity(nidVerificationResponse.getFaceSimilarity());
            ekycResponse.setFullName(nidVerificationResponse.getFullName());
            ekycResponse.setStatus("Citizen not found");
            return new ServiceResponse("Full verification needed", objectMapper.writeValueAsString(ekycResponse), false);
        }
        double faceSimilarity = Double.parseDouble(nidVerificationResponse.getFaceSimilarity().replaceAll("%", ""));
        if (faceSimilarity < 80.0) {
            log.info("FACE SIMILARITY IS LESS THAN 80%");
            ekycResponse.setCode(400);
            ekycResponse.setFaceSimilarity(nidVerificationResponse.getFaceSimilarity());
            ekycResponse.setFullName(nidVerificationResponse.getFullName());
            ekycResponse.setStatus("Similarity less than 80%");
            return new ServiceResponse("Image verification needed", objectMapper.writeValueAsString(ekycResponse), false);
        }
        log.info("FACE MATCHED FOR NID: {}", ekyc.getNid_no());
        ekycResponse.setCode(200);
        ekycResponse.setFaceSimilarity(nidVerificationResponse.getFaceSimilarity());
        ekycResponse.setFullName(nidVerificationResponse.getFullName());
        ekycResponse.setStatus("eKYC Done");
        return new ServiceResponse("NID verification successful", objectMapper.writeValueAsString(ekycResponse), false);

    }

    @Override
    public ServiceResponse extractNidData(MultipartFile nidFront, MultipartFile nidBack, MultipartFile photo, String investorCode, String boId) throws IOException {
        try {
            log.info("ENTERED SERVICE LAYER FOR EXTRACTING DATA...");
            Ekyc ekyc = accountDomain.callEkycService(nidFront, nidBack, photo, "ASTHA_USER");
            if (ekyc == null){
                log.info("FOUND NULL RESPONSE FROM KYC SERVICE...");
                return new ServiceResponse("Could not call Extraction Service");
            }
            accountDomain.saveImagesForEkyc(nidFront, nidBack, photo, investorCode, boId, ekyc.getNid_no());
            return new ServiceResponse("Data extracted successfully", objectMapper.writeValueAsString(ekyc));
        }
        catch (Exception e){
            log.info("EXCEPTION OCCURED WHILE SAVING NID IMAGES");
            e.printStackTrace();
            return new ServiceResponse("Something went wrong");
        }
    }

    @Override
    public ServiceResponse verifyNidAstha(String nidNumber, String dateOfBirth, MultipartFile photo) throws IOException {
        if (nidNumber == null || dateOfBirth == null){
            log.info("NID: {} DOB:{}", nidNumber, dateOfBirth);
            return new ServiceResponse("Full verification needed");
        }
        EkycResponse ekycResponse = EkycResponse.builder()
                .code(403).build();
//        NidVerificationResponse nidVerificationResponse = accountDomain.callNidVerification(nidNumber, dateOfBirth, photo, photo, "ASTHA");
        NidVerificationResponse nidVerificationResponse = this.demoResponseFromRVL();
        if (nidVerificationResponse == null){
            log.info("COULD NOT CALL API FOR NID VERIFICATION");
            ekycResponse.setStatus("Couldn’t Call RVL");
            ekycResponse.setCode(428);
            return new ServiceResponse("Full Verification needed", objectMapper.writeValueAsString(ekycResponse), true);
        }
        if (nidVerificationResponse.getCount() == 1) {
            log.info("API CALLED 1 TIMES ALREADY");
            ekycResponse.setCode(400);
            ekycResponse.setStatus("Called RVL with NID no: " + nidNumber);
            return new ServiceResponse("Full verification needed", objectMapper.writeValueAsString(ekycResponse), false);

        }
        if (!nidVerificationResponse.isSuccess()){
            log.info("DOB: {} NID: {}", dateOfBirth, nidNumber);
            ekycResponse.setCode(400);
            ekycResponse.setFaceSimilarity(nidVerificationResponse.getFaceSimilarity());
            ekycResponse.setFullName(nidVerificationResponse.getFullName());
            ekycResponse.setStatus("Citizen not found");
            return new ServiceResponse("Full verification needed", objectMapper.writeValueAsString(ekycResponse), false);
        }
        double faceSimilarity = Double.parseDouble(nidVerificationResponse.getFaceSimilarity().replaceAll("%", ""));
        if (faceSimilarity < 80.0) {
            log.info("FACE SIMILARITY IS LESS THAN 80%");
            ekycResponse.setCode(400);
            ekycResponse.setFaceSimilarity(nidVerificationResponse.getFaceSimilarity());
            ekycResponse.setFullName(nidVerificationResponse.getFullName());
            ekycResponse.setStatus("Similarity less than 80%");
            return new ServiceResponse("Image verification needed", objectMapper.writeValueAsString(ekycResponse), false);
        }
        log.info("FACE MATCHED FOR NID: {}", nidNumber);
        ekycResponse.setCode(200);
        ekycResponse.setFaceSimilarity(nidVerificationResponse.getFaceSimilarity());
        ekycResponse.setFullName(nidVerificationResponse.getFullName());
        ekycResponse.setStatus("eKYC Done");
        return new ServiceResponse("NID verification successful", objectMapper.writeValueAsString(ekycResponse), false);
    }

    @Override
    public ServiceResponse editAccount(PartialAccount partialAccount) throws JsonProcessingException {
        Account accountTarget = new Account();
        Account existingAccount = partialAccountDomain.findByMoBileNumber(partialAccount.getMobileNumber());
        if (existingAccount == null){
            return new ServiceResponse("Could not find account with this mobile number");
        }

        Account savedAccount = partialAccountDomain.save(this.updatePartialAccount(accountTarget, existingAccount), false);
        if (savedAccount == null){
            return new ServiceResponse("Could not edit account");
        }
        return new ServiceResponse("Account information saved successfully", objectMapper.writeValueAsString(savedAccount));
    }

    @Override
    public ServiceResponse searchAccount(String input) throws JsonProcessingException {
        Account account = accountDomain.searchByMobileOrEmailOrInvestorCode(input);
        if (account == null){
            return new ServiceResponse("Could not get any account");
        }
        return new ServiceResponse("Account information found", objectMapper.writeValueAsString(account));
    }

    private Account populateTOAccountObject(AccountOpeningDto accountOpeningDto, MultipartFile signature, MultipartFile chequeLeaf) throws IOException {
        return Account.builder()
                .name(accountOpeningDto.getName())
                .gender(accountOpeningDto.getGender())
                .nid(accountOpeningDto.getNid())
                .email(accountOpeningDto.getEmail())
                .mobileNumber(accountOpeningDto.getMobileNumber())
                .fathersName(accountOpeningDto.getFathersName())
                .mothersName(accountOpeningDto.getMothersName())
                .dateOfBirth(accountOpeningDto.getDateOfBirth())
                .investorCode(this.generateUniqueCode())
                .residency(accountOpeningDto.getResidency())
                .boType(accountOpeningDto.getBoType())
                .addressLine1(accountOpeningDto.getAddressLine1())
                .city(accountOpeningDto.getCity())
                .country(accountOpeningDto.getCountry())
                .state(accountOpeningDto.getState())
                .zipCode(accountOpeningDto.getZipCode())
                .bankName(accountOpeningDto.getBankName())
                .branchName(accountOpeningDto.getBranchName())
                .routingNumber(accountOpeningDto.getRoutingNumber())
                .accountNo(accountOpeningDto.getAccountNo())
//                .photo(this.convertToString(accountOpeningDto.getPhoto()))
//                .nidBack(this.convertToString(accountOpeningDto.getNidBack()))
//                .nidFront(this.convertToString(accountOpeningDto.getNidFront()))
                .signature(this.convertToString(signature))
                .chequeLeaf(this.convertToString(chequeLeaf))
                .build();
    }

    private Account populateToPartialAccountObject(AccountOpeningDto accountOpeningDto, MultipartFile signature, String accountId, String nidFront, String nidBack, MultipartFile chequeLeaf) throws IOException {
        return Account.builder()
                .id(accountId)
                .name(accountOpeningDto.getName())
                .gender(accountOpeningDto.getGender())
                .nid(accountOpeningDto.getNid())
                .email(accountOpeningDto.getEmail())
                .mobileNumber(accountOpeningDto.getMobileNumber())
                .fathersName(accountOpeningDto.getFathersName())
                .mothersName(accountOpeningDto.getMothersName())
                .dateOfBirth(accountOpeningDto.getDateOfBirth())
                .investorCode(this.generateUniqueCode())
                .residency(accountOpeningDto.getResidency())
                .boType(accountOpeningDto.getBoType())
                .addressLine1(accountOpeningDto.getAddressLine1())
                .city(accountOpeningDto.getCity())
                .country(accountOpeningDto.getCountry())
                .state(accountOpeningDto.getState())
                .zipCode(accountOpeningDto.getZipCode())
                .bankName(accountOpeningDto.getBankName())
                .branchName(accountOpeningDto.getBranchName())
                .routingNumber(accountOpeningDto.getRoutingNumber())
                .accountNo(accountOpeningDto.getAccountNo())
                .nidBack(nidBack)
                .nidFront(nidFront)
                .signature(this.convertToString(signature))
                .chequeLeaf(this.convertToString(chequeLeaf))
                .build();
    }


    private String convertToString(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path dirPath = Paths.get(uploadDir);
        Files.createDirectories(dirPath); // ensure the directory exists

        Path filePath = dirPath.resolve(fileName); // safely appends the filename
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }



    private BankDetails populateToBankDetails( String bankName, String branchName, String routingNumber){
        return BankDetails.builder()
                .branchName(branchName)
                .bankName(bankName)
                .routingNumber(routingNumber)
                .build();
    }

    private synchronized String generateUniqueCode() {
        int lastNumber = readLastNumber();
        int nextNumber = lastNumber + 1;

        String newCode = prefix + formatter.format(nextNumber);
        saveLastNumber(nextNumber);

        return newCode;
    }

    private int readLastNumber() {
        File file = new File(investorCodeDirectory);
        if (!file.exists()) {
            return 0;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String lastNumberStr = reader.readLine();
            return lastNumberStr != null ? Integer.parseInt(lastNumberStr) : 0;
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void saveLastNumber(int number) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(investorCodeDirectory))) {
            writer.write(String.valueOf(number));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JointAccoint populateToJointAccountModel(Account jointAccount){
        return JointAccoint.builder()
                .jointAccountname(jointAccount.getJointAccountname())
                .jointAccountEmail(jointAccount.getJointAccountEmail())
                .jointAccountMobileNumbr(jointAccount.getJointAccountMobileNumbr())
                .jointAccountAddress(jointAccount.getJointAccountAddress())
                .jointAccountPhoto(jointAccount.getJointAccountPhoto())
                .jointAccountSignature(jointAccount.getJointAccountSignature())
                .jointAccountNidFront(jointAccount.getJointAccountNidFront())
                .jointAccountNidBack(jointAccount.getJointAccountNidBack())
                .build();
    }

    private boolean makePartialAccountActive(Account account){
        Account partialAccount = partialAccountDomain.save(account, true);
        if (partialAccount == null ){
            return false;
        }
        return true;
    }

    private NidVerificationResponse demoResponseFromRVL(){
        List<NidVerificationResponse> nidVerificationResponseList = new ArrayList<>();
        nidVerificationResponseList.add(NidVerificationResponse.builder()
                .faceSimilarity("50.00%")
                .message("RANDOM MESSAGE")
                .success(true).build());
        nidVerificationResponseList.add(NidVerificationResponse.builder()
                .faceSimilarity("90.00%")
                .message("RANDOM MESSAGE")
                .success(true).build());
        nidVerificationResponseList.add(NidVerificationResponse.builder()
                .faceSimilarity("0%")
                .message("Could not find citizen")
                .success(false).build());
        nidVerificationResponseList.add(NidVerificationResponse.builder().build());
        Random random = new Random();
        int randomIndex = random.nextInt(3);
        return nidVerificationResponseList.get(randomIndex);
    }

    private Account updatePartialAccount(Account target, Account updates) {
        if (updates.getName() != null) target.setName(updates.getName());
        if (updates.getGender() != null) target.setGender(updates.getGender());
        if (updates.getNid() != null) target.setNid(updates.getNid());
        if (updates.getEmail() != null) target.setEmail(updates.getEmail());
        if (updates.getMobileNumber() != null) target.setMobileNumber(updates.getMobileNumber());
        if (updates.getFathersName() != null) target.setFathersName(updates.getFathersName());
        if (updates.getMothersName() != null) target.setMothersName(updates.getMothersName());
        if (updates.getDateOfBirth() != null) target.setDateOfBirth(updates.getDateOfBirth());
        if (updates.getResidency() != null) target.setResidency(updates.getResidency());
        if (updates.getBoType() != null) target.setBoType(updates.getBoType());
        if (updates.getAddressLine1() != null) target.setAddressLine1(updates.getAddressLine1());
        if (updates.getCity() != null) target.setCity(updates.getCity());
        if (updates.getCountry() != null) target.setCountry(updates.getCountry());
        if (updates.getState() != null) target.setState(updates.getState());
        if (updates.getZipCode() != null) target.setZipCode(updates.getZipCode());
        if (updates.getBankName() != null) target.setBankName(updates.getBankName());
        if (updates.getBranchName() != null) target.setBranchName(updates.getBranchName());
        if (updates.getRoutingNumber() != null) target.setRoutingNumber(updates.getRoutingNumber());
        if (updates.getAccountNo() != null) target.setAccountNo(updates.getAccountNo());
        return target;
    }




}
