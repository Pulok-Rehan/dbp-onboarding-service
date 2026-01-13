package com.bracepl.dbp_onboarding_service.domain.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.AccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.LiveValidationImage;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.PlatformProfile;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.LiveValidationImageRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.models.EditAccountRequest;
import com.bracepl.dbp_onboarding_service.adapter.out.models.NidVerificationResponse;
import com.bracepl.dbp_onboarding_service.adapter.out.services.SequenceGeneratorService;
import com.bracepl.dbp_onboarding_service.application.dtos.*;
import com.bracepl.dbp_onboarding_service.changeRequest.ChangeRequestEntity;
import com.bracepl.dbp_onboarding_service.config.SimpleMultipartFile;
import com.bracepl.dbp_onboarding_service.domain.enums.BoType;
import com.bracepl.dbp_onboarding_service.domain.interfaces.RmDomain;
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
    private final RmDomain rmDomain;
    private final LiveValidationImageRepository liveValidationImageRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    public AccountService(AccountDomain accountDomain, PartialAccountDomain partialAccountDomain, BankDomain bankDomain, ObjectMapper objectMapper, AccountCompletionDomain accountCompletionDomain, AuthService authService, RmDomain rmDomain, LiveValidationImageRepository liveValidationImageRepository, SequenceGeneratorService sequenceGeneratorService) {
        this.accountDomain = accountDomain;
        this.partialAccountDomain = partialAccountDomain;
        this.bankDomain = bankDomain;
        this.objectMapper = objectMapper;
        this.accountCompletionDomain = accountCompletionDomain;
        this.authService = authService;
        this.rmDomain = rmDomain;
        this.liveValidationImageRepository = liveValidationImageRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
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
            nidFront = Base64.getEncoder().encodeToString(temporaryData.getNidFront().getBytes());
            nidBack = Base64.getEncoder().encodeToString(temporaryData.getNidBack().getBytes());
            signatureUrl = Base64.getEncoder().encodeToString(temporaryData.getSignature().getBytes());
            chequeLeafUrl = Base64.getEncoder().encodeToString(temporaryData.getChequeLeaf().getBytes());
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
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(account.getMobileNumber(), account.getEmail(), true, true, true,true, false, true, true, true, false);
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
        if (temporaryData == null) {
            log.info("Could not get data");
            return new ServiceResponse("Could not open BO account");
        }
//        if (!Pattern.matches(emailRegex, temporaryData.getEmail())){
//            log.info("INVALID EMAIL PROVIDED");
//            return new ServiceResponse("Invalid email provided");
//        }
//        if (!Pattern.matches(mobileRegex, temporaryData.getMobileNumber())){
//            log.info("INVALID MOBILE NUMBER PROVIDED");
//            return new ServiceResponse("Invalid mobile number provided");
//        }
        if (accountDomain.isDuplicateAccount(temporaryData.getNid())) {
            log.info("DUPLICATE ACCOUNT FOUND WITH THIS NID {}", temporaryData.getNid());
            return new ServiceResponse("ALREADY HAS AN ACCOUNT WITH THIS NID");
        }
        if (!accountDomain.findByMobileNumber(temporaryData.getMobileNumber())) {
            log.info("DUPLICATE ACCOUNT FOUND WITH THIS MOBILE NUMBER {}", temporaryData.getMobileNumber());
            return new ServiceResponse("ALREADY HAS AN ACCOUNT WITH THIS Mobile Number");
        }
//        Ekyc ekyc = accountDomain.callEkycService(nidFront, nidBack, photo, temporaryData.getMobileNumber());
//        if (ekyc == null){
//            temporaryData.setNidVerified(false);
//        }
//        log.info("RESPONSE FROM KYC SERVER: {}", ekyc);
//
//                if (!ekyc.isMatched()){
//                    temporaryData.setNidVerified(false);
//        }
//        NidVerificationResponse nidVerificationResponse = accountDomain.callNidVerification(temporaryData.getNid(), temporaryData.getDateOfBirth(), temporaryData.getNidFront(), temporaryData.getNidBack(), "CLIENT_PORTAL");
//        if (nidVerificationResponse == null){
//            log.info("COULD NOT CALL API FOR NID VERIFICATION");
//            temporaryData.setNidVerified(false);
//        }
//        double faceSimilarity = Double.parseDouble(nidVerificationResponse.getFaceSimilarity().replaceAll("%", ""));
//        if (faceSimilarity < 50.0){
//            log.info("FACE SIMILARITY IS LESS THAN 50%");
//            temporaryData.setNidVerified(false);
//        }
        if (temporaryData.getJointAccountname() == null) {
            account = accountDomain.save(temporaryData);
        } else if (temporaryData.isBoLinked()) {
            account = accountDomain.saveWithBoLinked(temporaryData, temporaryData.getBoNumber());
        } else {
            account = accountDomain.saveWithJointAccount(temporaryData, this.populateToJointAccountModel(temporaryData));
        }
        if (account == null) {
            log.info("COULD NOT SAVE ACCOUNT DETAILS...");
            return new ServiceResponse("Could not open BO account");
        }
        log.info("ACCOUNT SAVED IN THE DATABASE");

        // Call SP service to open account
        CreateInvestorRequest spRequest = buildCreateInvestorRequest(temporaryData);
        boolean spCallSuccess = accountDomain.callSpServiceToOpenAccount(spRequest);
        if (!spCallSuccess) {
            log.info("COULD NOT CALL SP SERVICE TO OPEN ACCOUNT FOR: {}", temporaryData.getMobileNumber());
        } else {
            log.info("SP SERVICE CALLED SUCCESSFULLY FOR: {}", temporaryData.getMobileNumber());
        }
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(temporaryData.getMobileNumber(), temporaryData.getEmail(), true, true, true, true, false, true, true, true, false);
        if (completionSection == null) {
            log.info("COULD NOT SAVE ACCOUNT COMPLETION DETAILS IN THE DATABASE...");
            return new ServiceResponse("Could not save Account Completion details");
        }
        if (!this.makePartialAccountActive(temporaryData)) {
            log.info("COULD NOT MAKE PARTIAL ACCOUNT ACTIVE...");
        }
        log.info("ACCOUNT SAVED: {}", temporaryData);


        return new ServiceResponse("Account saved successfully", objectMapper.writeValueAsString(temporaryData.getMobileNumber()));
    }

    @Override
    public ServiceResponse openAccountPartial(AccountOpeningDto accountOpeningDto, MultipartFile signature, MultipartFile chequeLeaf) throws IOException {
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
    public ServiceResponse openAccountWithEkyc(MultipartFile photo, MultipartFile photoTiltingLeft,MultipartFile photoTiltingRight,MultipartFile photoSmiling,MultipartFile photoBlinking, RegisterDto registerDto) throws IOException {
        if (registerDto.getMobileNumber() == null || registerDto.getEmail() == null){
            log.info("NULL EMAIL OR MOBILE NUMBER PROVIDED");
            return new ServiceResponse("Mobile number or Email can not be empty.");
        }
        boolean boPayment =true;
        if (!boPayment){
            log.info("No payment found with THIS MOBILE NUMBER: {}", registerDto.getMobileNumber());
            return new ServiceResponse("BP payyment is not found with this mobile number.");
        }
        Account temporaryData = partialAccountDomain.findByMoBileNumber(registerDto.getMobileNumber());
        if (temporaryData != null && temporaryData.isActive()){
            log.info("THERE IS ALREADY AN SAVED ACCOUNT WITH THIS MOBILE NUMBER: {}", registerDto.getMobileNumber());
            return new ServiceResponse("You can not use this mobile number. Please use a different number.");
        }

        liveValidationImageRepository.save( LiveValidationImage.builder()
                .photoTiltingRight(Base64.getEncoder().encodeToString(photoTiltingRight.getBytes()))
                .photoTiltingLeft(Base64.getEncoder().encodeToString(photoTiltingLeft.getBytes()))
                .photoBlinking(Base64.getEncoder().encodeToString(photoBlinking.getBytes()))
                .photo(Base64.getEncoder().encodeToString(photo.getBytes()))
                .mobileNumber(registerDto.getMobileNumber()).build());


        Account account = Account.builder()
                .id(temporaryData == null ? null : temporaryData.getId())
                .photo(Base64.getEncoder().encodeToString(photo.getBytes()))
                .mobileNumber(registerDto.getMobileNumber())
                .email(registerDto.getEmail())
                .investorCode(sequenceGeneratorService.generateInvestorCode("investor_code"))
                .build();
        Account savedAccount = partialAccountDomain.save(account, false);
        if (savedAccount == null){
            log.info("COULD NOT SAVE PARTIAL DATA...");
            return new ServiceResponse("Could not save partial data");
        }
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(savedAccount.getMobileNumber(), savedAccount.getEmail(), true, false, false,false,false,false, false, false, false);
        if(completionSection == null){
            log.info("COULD NOT SAVE ACCOUNT COMPLETION DETAILS IN THE DATABASE...");
            return new ServiceResponse("Could not save Account Completion details");
        }
        else {
            account.setCompletionSection(completionSection);
        }
        Optional<PlatformProfile> platformProfile =
                accountCompletionDomain.getPlatformProfile(savedAccount.getMobileNumber());

        if (platformProfile.map(PlatformProfile::getImage).isEmpty()) {
            accountCompletionDomain.savePlatformProfile(
                    PlatformProfile.builder()
                            .mobileNumber(savedAccount.getMobileNumber())
                            .email(savedAccount.getEmail())
                            .image(Base64.getEncoder().encodeToString(photo.getBytes()))
                            .build()
            );
        }

        return new ServiceResponse("Information retrived successfully", objectMapper.writeValueAsString(savedAccount));
    }

    @Override
    public ServiceResponse openAccountWithDocuments(RegisterDto registerDto, MultipartFile nidFront, MultipartFile nidBack, MultipartFile tinCertificate, MultipartFile signature, MultipartFile chequeLeaf, MultipartFile boAttachment, MultipartFile jointAccountPicture, MultipartFile jointAccountSignature, MultipartFile jointNidFront, MultipartFile jointNidBack) throws IOException {
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
            temporaryData.setJointAccountSignature(Base64.getEncoder().encodeToString(jointAccountSignature.getBytes()));
            temporaryData.setJointAccountPhoto(Base64.getEncoder().encodeToString(jointAccountPicture.getBytes()));
            temporaryData.setJointAccountNidFront(Base64.getEncoder().encodeToString(jointNidFront.getBytes()));
            temporaryData.setJointAccountNidBack(Base64.getEncoder().encodeToString(jointNidBack.getBytes()));
        }
        temporaryData.setSignature(Base64.getEncoder().encodeToString(signature.getBytes()));
        temporaryData.setNidFront(Base64.getEncoder().encodeToString(nidFront.getBytes()));
        temporaryData.setNidBack(Base64.getEncoder().encodeToString(nidBack.getBytes()));
        temporaryData.setChequeLeaf(Base64.getEncoder().encodeToString(chequeLeaf.getBytes()));
        temporaryData.setTinCertificate(Base64.getEncoder().encodeToString(tinCertificate.getBytes()));
        Account savedAccount = partialAccountDomain.save(temporaryData, false);
        if (savedAccount == null){
            log.info("COULD NOT SAVE PARTIAL DATA...");
            return new ServiceResponse("Could not save partial data");
        }
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(temporaryData.getMobileNumber(), temporaryData.getEmail(), true, true, true,true,false,true, false, false, false);
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
        Account temporaryData = new Account();
        if (ValidationUtils.hasNullOrEmptyField(personalDetailsDto)) {
            log.info("NULL VALUE FOUND WHILE CHECKING NULL VALUE...");
            return new ServiceResponse("Null value found");
        }
        temporaryData = partialAccountDomain.findByNid(personalDetailsDto.getNid());
        if (temporaryData != null) {
            log.info("THERE IS ALREADY AN ACCOUNT WITH THIS NID: {}", personalDetailsDto.getNid());
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
        temporaryData.setPassportNumber(personalDetailsDto.getPassportNumber());
        temporaryData.setEmail(personalDetailsDto.getEmail());
        temporaryData.setMobileNumber(personalDetailsDto.getMobileNumber());
        temporaryData.setFathersName(personalDetailsDto.getFathersName());
        temporaryData.setMothersName(personalDetailsDto.getMothersName());
        temporaryData.setDateOfBirth(personalDetailsDto.getDateOfBirth());
        temporaryData.setResidency(personalDetailsDto.getResidency());
        temporaryData.setOccupation(personalDetailsDto.getOccupationDto().getOccupation());
        temporaryData.setSourceOfFund(personalDetailsDto.getOccupationDto().getSourceOfFund());
        temporaryData.setSalary(String.valueOf(personalDetailsDto.getOccupationDto().getSalary()));
        temporaryData.setAddressLine1PresentAddress(personalDetailsDto.getPresentAddress().getAddressLine1());
        temporaryData.setCityPresentAddress(personalDetailsDto.getPresentAddress().getCity());
        temporaryData.setCountryPresentAddress(personalDetailsDto.getPresentAddress().getCountry());
        temporaryData.setStatePresentAddress(personalDetailsDto.getPresentAddress().getState());
        temporaryData.setZipCodePresentAddress(personalDetailsDto.getPresentAddress().getZipCode());
        temporaryData.setAddressLine1PermanentAddress(personalDetailsDto.getPermanentAddress().getAddressLine1());
        temporaryData.setCityPermanentAddress(personalDetailsDto.getPermanentAddress().getCity());
        temporaryData.setCountryPermanentAddress(personalDetailsDto.getPermanentAddress().getCountry());
        temporaryData.setStatePermanentAddress(personalDetailsDto.getPermanentAddress().getState());
        temporaryData.setZipCodePernmanentAddress(personalDetailsDto.getPermanentAddress().getZipCode());
        Account savedAccount = partialAccountDomain.save(temporaryData, false);
        if (savedAccount == null) {
            log.info("COULD NOT SAVE PARTIAL DATA...");
            return new ServiceResponse("Could not save partial data");
        }
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(temporaryData.getMobileNumber(), temporaryData.getEmail(), true, true, false, false, false, false, false, false, false);
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
        Account temporaryData = partialAccountDomain.findByMoBileNumber("addressDto.getMobileNumber()");
        if (temporaryData == null) {
            log.info("COULD NOT SAVE ACCOOUNT INFORMATION WITH THIS MOBILE NUMBER: {}", "addressDto.getMobileNumber()");
            return new ServiceResponse("Could not save account information");
        }
                temporaryData.setAddressLine1PresentAddress(addressDto.getAddressLine1());
                temporaryData.setCityPresentAddress(addressDto.getCity());
                temporaryData.setCountryPresentAddress(addressDto.getCountry());
                temporaryData.setStatePresentAddress(addressDto.getState());
                temporaryData.setZipCodePresentAddress(addressDto.getZipCode());
        Account savedAccount = partialAccountDomain.save(temporaryData, false);
        if (savedAccount == null){
            log.info("COULD NOT SAVE PARTIAL DATA...");
            return new ServiceResponse("Could not save partial data");
        }
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(temporaryData.getMobileNumber(), temporaryData.getEmail(),
                true, true, true
                ,false,false,false, false, false, false);
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
        temporaryData.setRmId(bankDetailsDto.getRm());
        temporaryData.setPreferedBranch(bankDetailsDto.getPreferedBranch());
        temporaryData.setBoType(bankDetailsDto.getBoType());
        temporaryData.setBankName(bankDetailsDto.getBankName());
        temporaryData.setBranchName(bankDetailsDto.getBranchName());
        temporaryData.setRoutingNumber(bankDetailsDto.getRoutingNumber());
        temporaryData.setAccountNo(bankDetailsDto.getAccountNo());
        temporaryData.setBoLinked(bankDetailsDto.isBoLinked());
        temporaryData.setEnableDividendCredit(bankDetailsDto.isEnableDividendCredit());
        temporaryData.setApplyForTaxExemption(bankDetailsDto.isApplyForTaxExemption());
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
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(temporaryData.getMobileNumber(), temporaryData.getEmail(), true, true, true, true, false, false, false, false, false);
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
        CompletionSection completionSection = accountCompletionDomain.accountCompletion(savedAccount.getMobileNumber(), savedAccount.getEmail(), true, true, true,true,false,false, false, true, false);
        if(completionSection == null){
            log.info("COULD NOT SAVE ACCOUNT COMPLETION DETAILS IN THE DATABASE...");
            return new ServiceResponse("Could not save Account Completion details");
        }
        return new ServiceResponse("Information retrived successfully", objectMapper.writeValueAsString(savedAccount));
    }

    @Override
    public ServiceResponse openAccountWithNomineeDetails(NomineeListDto nomineeListDto) throws IOException {
        if (nomineeListDto == null || nomineeListDto.getNominees() == null) {
            log.info("NULL OR EMPTY NOMINEE LIST PROVIDED");
            return new ServiceResponse("Nominee details cannot be empty");
        }

        Account temporaryData = partialAccountDomain.findByMoBileNumber(nomineeListDto.getMobileNumber());
        if (temporaryData == null) {
            log.info("COULD NOT FIND ACCOUNT WITH THIS MOBILE NUMBER: {}", nomineeListDto.getMobileNumber());
            return new ServiceResponse("Could not find account information");
        }

        List<Nominee> nominees = new ArrayList<>();
        for (NomineeDto nomineeDto : nomineeListDto.getNominees()) {
            nominees.add(Nominee.builder()
                    .name(nomineeDto.getName())
                    .nid(nomineeDto.getNomineeNidNumber())
                    .percentage(nomineeDto.getNomineePercentage())
                    .relation(nomineeDto.getRelation())
                    .city(nomineeDto.getCity())
                    .country(nomineeDto.getCountry())
                    .state(nomineeDto.getState())
                    .zipCode(nomineeDto.getZipCode())
                    .address(nomineeDto.getAddress())
                    .mobileNumber(nomineeDto.getMobileNumber())
                    .minor(nomineeDto.isMinor())
                            .nomineeDob(nomineeDto.getNomineeDob())
                    .guardianNidNumber(nomineeDto.getGuardianNidNumber())
                    .nomineeNidFront(nomineeDto.getNomineeNidFront() != null ?  Base64.getEncoder().encodeToString(nomineeDto.getNomineeNidFront().getBytes())  : null)
                    .nomineeNidBack(nomineeDto.getNomineeNidBack() != null ? Base64.getEncoder().encodeToString(nomineeDto.getNomineeNidBack().getBytes() ): null)
                    .guardianNidFront(nomineeDto.getGuardianNidFront() != null ? Base64.getEncoder().encodeToString(nomineeDto.getGuardianNidFront().getBytes()) : null)
                    .guardianNidBack(nomineeDto.getGuardianNidBack() != null ? Base64.getEncoder().encodeToString(nomineeDto.getGuardianNidBack().getBytes()) : null)
                    .nomineePhoto(nomineeDto.getNomineePhoto() != null ? Base64.getEncoder().encodeToString(nomineeDto.getNomineePhoto().getBytes()) : null)
                    .nomineeSignature(nomineeDto.getNomineeSignature() != null ? Base64.getEncoder().encodeToString(nomineeDto.getNomineeSignature().getBytes()) : null)
                    .build());
        }

        Account savedAccount = partialAccountDomain.saveWithNominees(temporaryData, nominees);
        if (savedAccount == null) {
            log.info("COULD NOT SAVE PARTIAL DATA WITH NOMINEES...");
            return new ServiceResponse("Could not save nominee details");
        }

        CompletionSection completionSection = accountCompletionDomain.accountCompletion(
                savedAccount.getMobileNumber(),
                savedAccount.getEmail(),
                true, true, true, true, true, false, false, false, false
        );

        if (completionSection == null) {
            log.info("COULD NOT SAVE ACCOUNT COMPLETION DETAILS IN THE DATABASE...");
            return new ServiceResponse("Could not save Account Completion details");
        }

        return new ServiceResponse("Nominee information saved successfully", objectMapper.writeValueAsString(savedAccount));
    }

    @Override
    public ServiceResponse verifyNid(MultipartFile nidPhoto, MultipartFile photo) throws IOException {
        log.info("ENTERED SERVICE LAYER FOR EXTRACTING DATA...");
        ClassPathResource resource = new ClassPathResource("nidBack.jpg");
        // this.convertToString(nidPhoto); // This line is no longer needed
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
//        NidVerificationResponse nidVerificationResponse = accountDomain.callNidVerification(ekyc.getNid_no(), ekyc.getDate_of_birth(), photo, nidFront, "CLIENT_PORTAL");
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
    public ServiceResponse editAccount(EditAccountRequest changeRequestDto) throws IOException {
        Account existingAccount = partialAccountDomain.findById(changeRequestDto.getAccountId());
        if (existingAccount == null) {
            return new ServiceResponse("Could not find account with this account ID");
        }

        Account finalAccount = accountDomain.findByMobileNumberForForPowerOfAttorney(existingAccount.getMobileNumber());
        if (finalAccount == null) {
            return new ServiceResponse("Could not find account with this account ID");
        }

        Account updatedAccount = this.updateAccount(finalAccount, existingAccount);

        Account savedAccount = partialAccountDomain.save(updatedAccount, false);
        if (savedAccount == null) {
            return new ServiceResponse("Could not edit account");
        }

        return new ServiceResponse("Account information saved successfully",
                objectMapper.writeValueAsString(savedAccount));
    }


    @Override
    public ServiceResponse searchAccount(String input) throws JsonProcessingException {
        Account account = accountDomain.searchByMobileOrEmailOrInvestorCode(input);
        if (account == null){
            return new ServiceResponse("Could not get any account");
        }
        return new ServiceResponse("Account information found", objectMapper.writeValueAsString(account));
    }

    @Override
    public ServiceResponse getCompletionData(String mobileNumber) throws JsonProcessingException {
        CompletionSection completionSection = accountDomain.getAccountCompletionRate(mobileNumber);
        if (completionSection == null){
            log.info("COULD NOT FIND ANY ACCOUNT WITH THIS MOBILE NUMBER: {} IN COMPLETION DATA TABLE", mobileNumber);
            return new ServiceResponse("Could not get dashboard information", objectMapper.writeValueAsString(this.makeFalseCompletionSection(mobileNumber)));
        }
        Account temporaryData = partialAccountDomain.findByMoBileNumber(mobileNumber);
        if (temporaryData == null){
            log.info("COULD NOT FIND ANY ACCOUNT WITH THIS MOBILE NUMBER: {} IN PARTIAL ACCOUNT DATA TABLE", mobileNumber);
            return new ServiceResponse("Could not get account information");
        }
        completionSection.setPartialAccount(temporaryData);
        List<ChangeRequestEntity> changeRequest = accountDomain.getChangeRequest(mobileNumber);
        if ( changeRequest == null){
            completionSection.setChangeRequest(new ArrayList<>());
        }
        completionSection.setChangeRequest(changeRequest);
        log.info("COMPLETION SECTION RESPONSE WITH PARTIAL ACCOUNT INFORMATION: {}", completionSection);
        return new ServiceResponse("DashboardInformation information found", objectMapper.writeValueAsString(completionSection));

    }

    @Override
    public ServiceResponse boPayment(String mobileNumber) throws JsonProcessingException {
        CompletionSection completionSection = accountDomain.getAccountCompletionRate(mobileNumber);
        if (completionSection == null){
            return new ServiceResponse("Could not add completion section");
//            completionSection = accountCompletionDomain.accountCompletion(mobileNumber, "", false, false, false, false, false, false, false, true, false);
        }
        completionSection.setBoPayment(true);
        completionSection = accountCompletionDomain.accountCompletion(completionSection.getMobileNumber(), completionSection.getEmailAddress(), completionSection.isLiveVerificationPhotos(), completionSection.isPersonalDetails(), completionSection.isAddress(), completionSection.isBankDetails(), completionSection.isNomineeDetails(), completionSection.isDocuments(), false, true, false);
        if (completionSection == null){
            return new ServiceResponse("Could not add completion section");
        }
        return new ServiceResponse("Completion section added", objectMapper.writeValueAsString(completionSection));
    }

    @Override
    public AccountEntity getAccountSnapshot(String input) throws JsonProcessingException {
        AccountEntity account = accountDomain.searchByMobile(input);
        if (account == null) {
            return null; // Or throw an exception, or return an empty DTO
        }
//
//        Map<String, Object> bankDetails = new HashMap<>();
//        if (account.getBankName() != null) bankDetails.put("bankName", account.getBankName());
//        if (account.getBranchName() != null) bankDetails.put("branchName", account.getBranchName());
//        if (account.getRoutingNumber() != null) bankDetails.put("routingNumber", account.getRoutingNumber());
//
//        List<Map<String, Object>> nominees = new ArrayList<>();
//        if (account.getNominees() != null) {
//            for (Nominee nominee : account.getNominees()) {
//                Map<String, Object> nomineeMap = new HashMap<>();
//                nomineeMap.put("name", nominee.getName());
//                nomineeMap.put("nomineeNidNumber", nominee.getNomineeNidNumber());
//                nomineeMap.put("nomineePercentage", nominee.getNomineePercentage());
//                nomineeMap.put("relation", nominee.getRelation());
//                nomineeMap.put("city", nominee.getCity());
//                nomineeMap.put("country", nominee.getCountry());
//                nomineeMap.put("state", nominee.getState());
//                nomineeMap.put("zipCode", nominee.getZipCode());
//                nomineeMap.put("address", nominee.getAddress());
//                nomineeMap.put("mobileNumber", nominee.getMobileNumber());
//                // Note: NID images (byte[]) are not included in the snapshot for brevity,
//                // but can be added if needed (e.g., as Base64 encoded strings).
//                nominees.add(nomineeMap);
//            }
//        }
//
//        return AccountSnapshotDto.builder()
//                .emailAddress(account.getEmail())
//                .mobileNumber(account.getMobileNumber())
//                .addressLine1(account.getAddressLine1PresentAddress())
//                .city(account.getCityPresentAddress())
//                .country(account.getCountryPresentAddress())
//                .state(account.getStatePresentAddress())
//                .zipCode(account.getZipCodePresentAddress())
//                .accountNo(account.getAccountNo())
//                .bankDetails(bankDetails)
//                .nominees(nominees)
//                .build();

        return account;
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
                .addressLine1PresentAddress(accountOpeningDto.getAddressLine1())
                .cityPresentAddress(accountOpeningDto.getCity())
                .countryPresentAddress(accountOpeningDto.getCountry())
                .statePresentAddress(accountOpeningDto.getState())
                .zipCodePresentAddress(accountOpeningDto.getZipCode())
                .bankName(accountOpeningDto.getBankName())
                .branchName(accountOpeningDto.getBranchName())
                .routingNumber(accountOpeningDto.getRoutingNumber())
                .accountNo(accountOpeningDto.getAccountNo())
                .signature( Base64.getEncoder().encodeToString(signature.getBytes()))
                .chequeLeaf( Base64.getEncoder().encodeToString(chequeLeaf.getBytes()))
                .build();
    }

    private Account populateToPartialAccountObject(AccountOpeningDto accountOpeningDto, MultipartFile signature, String accountId, String nidFront,String nidBack, MultipartFile chequeLeaf) throws IOException {
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
                .addressLine1PresentAddress(accountOpeningDto.getAddressLine1())
                .cityPresentAddress(accountOpeningDto.getCity())
                .countryPresentAddress(accountOpeningDto.getCountry())
                .statePresentAddress(accountOpeningDto.getState())
                .zipCodePresentAddress(accountOpeningDto.getZipCode())
                .bankName(accountOpeningDto.getBankName())
                .branchName(accountOpeningDto.getBranchName())
                .routingNumber(accountOpeningDto.getRoutingNumber())
                .accountNo(accountOpeningDto.getAccountNo())
                .nidBack(nidBack)
                .nidFront(nidFront)
                .signature(Base64.getEncoder().encodeToString(signature.getBytes()))
                .chequeLeaf(Base64.getEncoder().encodeToString(chequeLeaf.getBytes()))
                .build();
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

    private Account updateAccount(Account target, Account updates) {
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
        if (updates.getAddressLine1PresentAddress() != null) target.setAddressLine1PresentAddress(updates.getAddressLine1PresentAddress());
        if (updates.getCityPresentAddress() != null) target.setCityPresentAddress(updates.getCityPresentAddress());
        if (updates.getCountryPresentAddress() != null) target.setCountryPresentAddress(updates.getCountryPresentAddress());
        if (updates.getStatePresentAddress() != null) target.setStatePresentAddress(updates.getStatePresentAddress());
        if (updates.getZipCodePresentAddress() != null) target.setZipCodePresentAddress(updates.getZipCodePresentAddress());
        if (updates.getBankName() != null) target.setBankName(updates.getBankName());
        if (updates.getBranchName() != null) target.setBranchName(updates.getBranchName());
        if (updates.getRoutingNumber() != null) target.setRoutingNumber(updates.getRoutingNumber());
        if (updates.getAccountNo() != null) target.setAccountNo(updates.getAccountNo());
        return target;
    }

    private CompletionSection makeFalseCompletionSection(String mobileNumber){
        return CompletionSection.builder()
                .mobileNumber(mobileNumber)
                .documents(false)
                .liveVerificationPhotos(false)
                .personalDetails(false)
                .address(false)
                .bankDetails(false)
                .nomineeDetails(false)
                .partialAccount(Account.builder()
                        .mobileNumber(mobileNumber)
                        .active(false)
                        .build())
                .build();
    }

    private void updatePartialAccount(Account target, EditAccountRequest request) throws IOException {
        // PERSONAL DETAILS SECTION
        if (request.isPersonalDetailsSection() && request.getPersonalDetailsDto() != null) {
            var dto = request.getPersonalDetailsDto();

            if (dto.getName() != null) target.setName(dto.getName());
            if (dto.getGender() != null) target.setGender(dto.getGender());
            if (dto.getNid() != null) target.setNid(dto.getNid());
            if (dto.getFathersName() != null) target.setFathersName(dto.getFathersName());
            if (dto.getMothersName() != null) target.setMothersName(dto.getMothersName());
            if (dto.getDateOfBirth() != null) target.setDateOfBirth(dto.getDateOfBirth().toString());
            if (dto.getResidency() != null) target.setResidency(dto.getResidency());
        }

        // ADDRESS SECTION
        if (request.isAddressSection() && request.getAddressDto() != null) {
            var dto = request.getAddressDto();

            if (dto.getAddressLine1() != null) target.setAddressLine1PresentAddress(dto.getAddressLine1());
            if (dto.getCity() != null) target.setCityPresentAddress(dto.getCity());
            if (dto.getCountry() != null) target.setCountryPresentAddress(dto.getCountry());
            if (dto.getState() != null) target.setStatePresentAddress(dto.getState());
            if (dto.getZipCode() != null) target.setZipCodePresentAddress(dto.getZipCode());
        }

        // BANK DETAILS SECTION
        if (request.isBankDetailsSection() && request.getBankDetailsDto() != null) {
            var dto = request.getBankDetailsDto();

            if (dto.getBankName() != null) target.setBankName(dto.getBankName());
            if (dto.getBranchName() != null) target.setBranchName(dto.getBranchName());
            if (dto.getRoutingNumber() != null) target.setRoutingNumber(dto.getRoutingNumber());
            if (dto.getAccountNo() != null) target.setAccountNo(dto.getAccountNo());
        }

        // DOCUMENTS SECTION
        if (request.isDocumentsSection() && request.getDocumentsDto() != null) {
            var dto = request.getDocumentsDto();
            if (dto.getPhoto() != null) target.setPhoto(Base64.getEncoder().encodeToString(dto.getPhoto().getBytes()));
            if (dto.getSignature() != null) target.setSignature(Base64.getEncoder().encodeToString(dto.getSignature().getBytes()));
            if (dto.getChequeLeaf() != null) target.setChequeLeaf(Base64.getEncoder().encodeToString(dto.getChequeLeaf().getBytes()));
        }
    }

    private CreateInvestorRequest buildCreateInvestorRequest(Account account) {
        return CreateInvestorRequest.builder()
                .investorCode(account.getInvestorCode())
                .boType(account.getBoType())
                .firstName(account.getName())
                .shortName(account.getName())
                .addressLine1(account.getAddressLine1PresentAddress())
                .addressLine2(account.getStatePresentAddress())
                .addressLine3(account.getCountryPresentAddress())
                .city(account.getCityPresentAddress())
                .country(account.getCountryPresentAddress())
                .postalCode(account.getZipCodePresentAddress())
                .nationalId(account.getNid())
                .mobile(account.getMobileNumber())
                .email(account.getEmail())
                .gender(account.getGender())
                .occupation(account.getOccupation())
                .fatherOrHusbandName(account.getFathersName())
                .motherName(account.getMothersName())
                .bankRoutingNumber(account.getRoutingNumber())
                .bankAccountNumber(account.getAccountNo())
                .tin(account.getTinCertificate())
                .residencyFlag(account.getResidency())
                .dateOfBirth(account.getDateOfBirth())
                .createdBy("CLIENT_PORTAL")
                .build();
    }


}
