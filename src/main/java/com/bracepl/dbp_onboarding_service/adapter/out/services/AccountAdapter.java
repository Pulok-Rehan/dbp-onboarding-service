package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.*;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.*;
import com.bracepl.dbp_onboarding_service.adapter.out.models.NidVerificationResponse;
import com.bracepl.dbp_onboarding_service.changeRequest.ChangeRequestEntity;
import com.bracepl.dbp_onboarding_service.changeRequest.repo.ChangeRequestRepository;
import com.bracepl.dbp_onboarding_service.domain.enums.AccountStatus;
import com.bracepl.dbp_onboarding_service.domain.interfaces.AccountDomain;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.CompletionSection;
import com.bracepl.dbp_onboarding_service.domain.models.Ekyc;
import com.bracepl.dbp_onboarding_service.domain.models.JointAccoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.*;


@Component
@Slf4j
public class AccountAdapter implements AccountDomain {

    @Value("${nidVerification.baseUrl}")
    private String nidBaseUrl;

    @Value("${nidVerification.apiKey}")
    private String xApiKey;

    @Value("${nidVerification.origin}")
    private String origin;

    @Value("${nidVerification.verification}")
    private String verification;

    @Value("${nidVerification.dir}")
    private String uploadDir;

    private final AccountRepository accountRepository;
    private final EkycService ekycService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final NidVerificationRepository nidVerificationRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final CsdRepository csdRepository;
    private final AccountCompletionRepository accountCompletionRepository;
    private final ChangeRequestRepository changeRequestRepository;

    public AccountAdapter(AccountRepository accountRepository, EkycService ekycService, RestTemplate restTemplate, ObjectMapper objectMapper, NidVerificationRepository nidVerificationRepository, SequenceGeneratorService sequenceGeneratorService, CsdRepository csdRepository, AccountCompletionRepository accountCompletionRepository, ChangeRequestRepository changeRequestRepository) {
        this.accountRepository = accountRepository;
        this.ekycService = ekycService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.nidVerificationRepository = nidVerificationRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.csdRepository = csdRepository;
        this.accountCompletionRepository = accountCompletionRepository;
        this.changeRequestRepository = changeRequestRepository;
    }

    @Override
    public Account save(Account account) {
        try {
            List<CsdEntity> csdEntities = csdRepository.findAll();
            if (!csdEntities.isEmpty()) {
                Random random = new Random();
                int randomIndex = random.nextInt(csdEntities.size());
                String csdId = csdEntities.get(randomIndex).getId();
                AccountEntity accountEntity = this.populateToAccountEntity(account, csdId);
                if (accountEntity.getInvestorCode() == null){
                    accountEntity.setInvestorCode(sequenceGeneratorService.generateInvestorCode("investor_code"));
                }
                AccountEntity savedAccountEntity = accountRepository.save(accountEntity);
                return addIdToAccountObject(account, savedAccountEntity.getId());// assuming getId() exists
            }
            else return null;

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Account saveWithBoLinked(Account account, String boNumber) {
        try {
            List<CsdEntity> csdEntities = csdRepository.findAll();
            if (!csdEntities.isEmpty()) {
                Random random = new Random();
                int randomIndex = random.nextInt(csdEntities.size());
                String csdId = csdEntities.get(randomIndex).getId();
                AccountEntity accountEntity = this.populateToAccountEntity(account, csdId);
                if (accountEntity.getInvestorCode() == null){
                    accountEntity.setInvestorCode(sequenceGeneratorService.generateInvestorCode("investor_code"));
                }
                AccountEntity savedAccountEntity = accountRepository.save(this.populateToAccountEntityWithBoLinked(account, boNumber));
                return addIdToAccountObject(account, savedAccountEntity.getId());// assuming getId() exists
            }
            else return null;

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Account saveWithJointAccount(Account account, JointAccoint jointAccoint) {
        try {
            List<CsdEntity> csdEntities = csdRepository.findAll();
            if (!csdEntities.isEmpty()) {
                Random random = new Random();
                int randomIndex = random.nextInt(csdEntities.size());
                String csdId = csdEntities.get(randomIndex).getId();
                AccountEntity accountEntity = this.populateToAccountEntity(account, csdId);
                if (accountEntity.getInvestorCode() == null){
                    accountEntity.setInvestorCode(sequenceGeneratorService.generateInvestorCode("investor_code"));
                }
                AccountEntity savedAccountEntity = accountRepository.save(this.populateToAccountEntityWithJointAccount(account, this.populateToJointAccountEntity(jointAccoint)));
                return addIdToAccountObject(account, savedAccountEntity.getId());
            }
            else return null;

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isDuplicateAccount(String nidNumber) {
        try {
            Optional<AccountEntity> optionalAccountEntity = accountRepository.findByNid(nidNumber);
            return optionalAccountEntity.isPresent();
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Account findByInvestorCode(String investorCode) {
        try {
            Optional<AccountEntity> optionalAccountEntity = accountRepository.findByInvestorCode(investorCode);
            if (optionalAccountEntity.isPresent()){
                return this.populateToAccountModel(optionalAccountEntity.get());
            }
            return null;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public boolean findByMobileNumber(String mobileNumber) {
        try {
            Optional<AccountEntity> optionalAccountEntity = accountRepository.findByMobileNumber(mobileNumber);
            if (optionalAccountEntity.isEmpty()){
                return true;
            }
            return false;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Account findById(String id) {
        try {
            Optional<AccountEntity> optionalAccountEntity = accountRepository.findById(id);
            if (optionalAccountEntity.isPresent()){
                return this.populateToAccountModel(optionalAccountEntity.get());
            }
            return null;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

//    @Override
//    public Account findByEmail(String email) {
//        try {
//            Optional<AccountEntity> optionalAccountEntity = accountRepository.findByEmailAddress(email);
//            if (optionalAccountEntity.isPresent()){
//                return this.populateToAccountModel(optionalAccountEntity.get());
//            }
//            return null;
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            return null;
//        }
//    }

    @Override
    public Account findByMobileNumberForForgetPassword(String mobileNumber) {
        try {
            Optional<AccountEntity> optionalAccountEntity = accountRepository.findByMobileNumber(mobileNumber);
            if (optionalAccountEntity.isPresent()){
                return this.populateToAccountModel(optionalAccountEntity.get());
            }
            return null;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Account findByMobileNumberForForPowerOfAttorney(String mobileNumber) {
        try {
            Optional<AccountEntity> optionalAccountEntity = accountRepository.findByMobileNumber(mobileNumber);
            if (optionalAccountEntity.isPresent()){
                return this.populateToAccountModel(optionalAccountEntity.get());
            }
            return null;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Account searchByMobileOrEmailOrInvestorCode(String input) {
        Optional<AccountEntity> optionalAccountEntity = accountRepository.findByMobileOrEmailOrInvestorCode(input);
        if (optionalAccountEntity.isEmpty()){
            return new Account();
        }
        return this.populateToAccountModel(optionalAccountEntity.get());
    }

    @Override
    public Ekyc callEkycService(MultipartFile nidFront, MultipartFile nidBack, MultipartFile photo, String mobileNumber) throws IOException {
        return ekycService.callEkyc(nidFront, nidBack, mobileNumber);
    }

    @Override
    public NidVerification saveImagesForEkyc(MultipartFile nidFront, MultipartFile nidBack, MultipartFile photo, String investorCode, String boId, String nidNumber) throws IOException {
        return nidVerificationRepository.save(NidVerification.builder()
                        .nidNumber(nidNumber)
                        .investorCode(investorCode)
                        .boId(boId)
                        .nidFront(this.convertToString(nidFront))
                        .nidBack(this.convertToString(nidBack))
                        .photo(this.convertToString(photo))
                .build());
    }

    @Override
    public NidVerificationResponse callNidVerification(String nidNumber, String dateOfBirth, MultipartFile photo, MultipartFile nidPhoto, String channel) {
        log.info("ENTERED FOR CALLING RVL API...");
        String apiUrl = nidBaseUrl+verification;
        String apiKey = xApiKey;

        List<NidVerificationResponse> nidVerificationResponses = nidVerificationRepository.findAllByNidNumber(nidNumber);

        if (!nidVerificationResponses.isEmpty()) {
            return NidVerificationResponse.builder()
                    .faceSimilarity(nidVerificationResponses.get(0).getFaceSimilarity())
                    .success(nidVerificationResponses.get(0).isSuccess())
                    .count(1).build();
        }


        try {
            String imageBase64 = Base64.getEncoder().encodeToString(photo.getBytes());


            String requestBody = String.format("""
                    {
                        "nationalNumber": "%s",
                        "dateOfBirth": "%s",
                        "imageData": "%s"
                    }
                    """, nidNumber, dateOfBirth, imageBase64);


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .header("Origin", origin)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            request.headers().map().forEach((key, values) -> {
                System.out.println(key + ": " + String.join(", ", values));
            });

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("RESPONSE FROM IVL: {}", objectMapper.writeValueAsString(response.body()));
//            log.info("AFTER CALLING RVL API: {}", LocalDateTime.now());
            NidVerificationResponse nidResponse = objectMapper.readValue(response.body(), NidVerificationResponse.class);
            log.info("AFTER MAPPING RESPONSE: {}", objectMapper.writeValueAsString(nidResponse));
            nidVerificationRepository.save(NidVerification.builder()
                    .faceSimilarity(nidResponse.getFaceSimilarity())
                    .channel(channel)
                    .nidNumber(nidNumber)
                    .message(nidResponse.getMessage() == null ? "Could not call API" : nidResponse.getMessage())
                    .photo(this.convertToString(photo))
                    .nidFront(this.convertToString(nidPhoto))
                    .success(nidResponse.isSuccess()).build());
            return nidResponse;

        } catch (Exception e) {
            e.printStackTrace();
//            nidVerificationRepository.save(NidVerification.builder()
//                    .nidNumber(nidNumber)
//                    .dateOfBirth(e.getMessage())
//                    .faceSimilarity(e.getMessage())
//                    .channel(channel)
//                    .message(e.getMessage())
//                    .success(false)
//                    .build());
            return null;
        }
    }

    @Override
    public CompletionSection getAccountCompletionRate(String mobileNumber) {
        try {
            CompletionSection completionSection;
            Optional<CompletionSectionEntity> optionalCompletionSectionEntity = accountCompletionRepository.findByMobileNumber(mobileNumber);
            if (optionalCompletionSectionEntity.isPresent()){
                completionSection = this.populateToCompletionSection(optionalCompletionSectionEntity.get());
                return completionSection;
            }
            return null ;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ChangeRequestEntity> getChangeRequest(String mobileNumber) {
        List<ChangeRequestEntity> changeRequest = changeRequestRepository.findByRequestedFor(mobileNumber);
        if (changeRequest.isEmpty()){
            return null;
        }
        return changeRequest;
    }

    private AccountEntity populateToAccountEntity(Account account, String csdId){
        return AccountEntity.builder()
//                .id(account.getId())
                .investorCode(account.getInvestorCode())
                .name(account.getName())
                .emailAddress(account.getEmail())
                .mobileNumber(account.getMobileNumber())
                .gender(account.getGender())
                .nid(account.getNid())
                .fathersName(account.getFathersName())
                .mothersName(account.getMothersName())
                .dateOfBirth(account.getDateOfBirth())
                .addressLine1(account.getAddressLine1())
                .city(account.getCity())
                .country(account.getCountry())
                .state(account.getState())
                .zipCode(account.getZipCode())
                .bank(BankEntity.builder()
                        .bankName(account.getBankName())
                        .routingNumber(account.getRoutingNumber())
                        .branchName(account.getBranchName())
                        .build())
                .accountNo(account.getAccountNo())
                .residency(account.getResidency())
                .boType(account.getBoType())
                .nidFront(account.getNidFront())
                .nidBack(account.getNidBack())
                .photo(account.getPhoto())
                .signature(account.getSignature())
                .chequeLeaf(account.getChequeLeaf())
                .csdId(csdId)
                .accountStatus(AccountStatus.REQUESTED)
                .build();
    }

    private AccountEntity populateToAccountEntityWithBoLinked(Account account, String boNumber){
        return AccountEntity.builder()
                .id(account.getId())
                .investorCode(account.getInvestorCode())
                .name(account.getName())
                .emailAddress(account.getEmail())
                .mobileNumber(account.getMobileNumber())
                .gender(account.getGender())
                .nid(account.getNid())
                .fathersName(account.getFathersName())
                .mothersName(account.getMothersName())
                .dateOfBirth(account.getDateOfBirth())
                .addressLine1(account.getAddressLine1())
                .city(account.getCity())
                .country(account.getCountry())
                .state(account.getState())
                .zipCode(account.getZipCode())
                .bank(BankEntity.builder()
                        .bankName(account.getBankName())
                        .routingNumber(account.getRoutingNumber())
                        .branchName(account.getBranchName())
                        .build())
                .accountNo(account.getAccountNo())
                .residency(account.getResidency())
                .boType(account.getBoType())
                .nidFront(account.getNidFront())
                .nidBack(account.getNidBack())
                .photo(account.getPhoto())
                .signature(account.getSignature())
                .chequeLeaf(account.getChequeLeaf())
                .boNumber(boNumber)
                .build();
    }

    private AccountEntity populateToAccountEntityWithJointAccount(Account account, JointAccountEntity jointAccountEntity){
        return AccountEntity.builder()
                .id(account.getId())
                .investorCode(account.getInvestorCode())
                .name(account.getName())
                .emailAddress(account.getEmail())
                .mobileNumber(account.getMobileNumber())
                .gender(account.getGender())
                .nid(account.getNid())
                .fathersName(account.getFathersName())
                .mothersName(account.getMothersName())
                .dateOfBirth(account.getDateOfBirth())
                .addressLine1(account.getAddressLine1())
                .city(account.getCity())
                .country(account.getCountry())
                .state(account.getState())
                .zipCode(account.getZipCode())
                .bank(BankEntity.builder()
                        .bankName(account.getBankName())
                        .routingNumber(account.getRoutingNumber())
                        .branchName(account.getBranchName())
                        .build())
                .accountNo(account.getAccountNo())
                .residency(account.getResidency())
                .boType(account.getBoType())
                .nidFront(account.getNidFront())
                .nidBack(account.getNidBack())
                .photo(account.getPhoto())
                .signature(account.getSignature())
                .chequeLeaf(account.getChequeLeaf())
                .jointAccountEntity(jointAccountEntity)
                .build();
    }

    private JointAccountEntity populateToJointAccountEntity(JointAccoint jointAccoint){
        return JointAccountEntity.builder()
                .name(jointAccoint.getJointAccountname())
                .email(jointAccoint.getJointAccountEmail())
                .mobileNumbr(jointAccoint.getJointAccountMobileNumbr())
                .address(jointAccoint.getJointAccountAddress())
                .jointAccountPhoto(jointAccoint.getJointAccountPhoto())
                .jointAccountSignature(jointAccoint.getJointAccountSignature())
                .jointAccountNidFront(jointAccoint.getJointAccountNidFront())
                .jointAccountNidBack(jointAccoint.getJointAccountNidBack())
                .build();
    }

    private Account populateToAccountModel(AccountEntity account){
        return Account.builder()
                .id(account.getId())
                .investorCode(account.getInvestorCode())
                .email(account.getEmailAddress())
                .mobileNumber(account.getMobileNumber())
                .name(account.getName())
                .gender(account.getGender())
                .nid(account.getNid())
                .csdId(account.getCsdId())
                .build();
    }

    private Account addIdToAccountObject(Account account, String id){
        account.setId(id);
        return account;
    }
    private AccountEntity populateToAccountModelForPOA(AccountEntity account){
        return AccountEntity.builder()
                .id(account.getId())
                .investorCode(account.getInvestorCode())
                .name(account.getName())
                .emailAddress(account.getEmailAddress())
                .mobileNumber(account.getMobileNumber())
                .gender(account.getGender())
                .nid(account.getNid())
                .fathersName(account.getFathersName())
                .mothersName(account.getMothersName())
                .dateOfBirth(account.getDateOfBirth())
                .addressLine1(account.getAddressLine1())
                .city(account.getCity())
                .country(account.getCountry())
                .state(account.getState())
                .zipCode(account.getZipCode())
                .bank(BankEntity.builder()
                        .bankName(account.getBank().getBankName())
                        .routingNumber(account.getBank().getRoutingNumber())
                        .branchName(account.getBank().getBranchName())
                        .build())
                .accountNo(account.getAccountNo())
                .residency(account.getResidency())
                .boType(account.getBoType())
                .nidFront(account.getNidFront())
                .nidBack(account.getNidBack())
                .photo(account.getPhoto())
                .signature(account.getSignature())
                .chequeLeaf(account.getChequeLeaf())
                .boNumber(account.getBoNumber())
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

    private CompletionSection populateToCompletionSection(CompletionSectionEntity completionSection){
        return CompletionSection.builder()
                .mobileNumber(completionSection.getMobileNumber())
                .personalDetails(completionSection.isPersonalDetails())
                .address(completionSection.isAddress())
                .bankDetails(completionSection.isBankDetails())
                .nomineeDetails(completionSection.isNomineeDetails())
                .nidPhotos(completionSection.isNidPhotos())
                .documents(completionSection.isDocuments())
                .build();
    }
}
