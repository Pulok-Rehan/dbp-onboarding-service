package com.bracepl.dbp_onboarding_service.domain.services;

import com.bracepl.dbp_onboarding_service.domain.interfaces.AccountDomain;
import com.bracepl.dbp_onboarding_service.domain.interfaces.NotificationDomain;
import com.bracepl.dbp_onboarding_service.domain.interfaces.PowerOfAttorneyUseCase;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PowerOfAttorneyService implements PowerOfAttorneyUseCase {
    private final AccountDomain accountDomain;
    private final ObjectMapper objectMapper;
    private final NotificationDomain notificationDomain;

    public PowerOfAttorneyService(AccountDomain accountDomain, ObjectMapper objectMapper, NotificationDomain notificationDomain) {
        this.accountDomain = accountDomain;
        this.objectMapper = objectMapper;
        this.notificationDomain = notificationDomain;
    }

    @Override
    public ServiceResponse grantPowerOfAttorney(String mobileNumber, String granteeId, String otp) throws JsonProcessingException {
        List<String> powerOfAttorneyForList = new ArrayList<>();
        List<String> powerOfAttorneyByList = new ArrayList<>();
        Account account = accountDomain.findByMobileNumberForForPowerOfAttorney(mobileNumber);
        if (account == null){
            log.info("COULD NOT FIND ACCOUNT WITH THIS MOBILE NUMBER: {}", mobileNumber);
            return new ServiceResponse("Could not find Account with this mobile number");
        }
        if (account.getPowerOfAttorneyByAccounts() == null){
            log.info("THERE WAS NO POWER OF ATTORNEY TO THIS ACCOUNT...");
            powerOfAttorneyByList.add(granteeId);
            account.setPowerOfAttorneyByAccounts(powerOfAttorneyByList);
        }
        else {
            log.info("ADDING TO POWER OF ATTORNEY LIST...");
            powerOfAttorneyByList = account.getPowerOfAttorneyByAccounts();
            powerOfAttorneyByList.add(granteeId);
            account.setPowerOfAttorneyByAccounts(powerOfAttorneyByList);
        }
        Account granteeAccount = accountDomain.findById(granteeId);
        if (granteeAccount == null){
            log.info("THERE WAS NO GRANTEE ACCOUNT WITH THIS ID: {}", granteeId);
            return new ServiceResponse("Could not find account ");
        }
        if (granteeAccount.getPowerOfAttorneyForAccounts() == null){
            log.info("THERE WAS NO POWER OF ATTORNEY OF THIS ACCOUNT...");
            powerOfAttorneyByList.add(granteeId);
            granteeAccount.setPowerOfAttorneyByAccounts(powerOfAttorneyForList);
        }
        else {
            log.info("ADDING TO POWER OF ATTORNEY LIST...");
            powerOfAttorneyByList = granteeAccount.getPowerOfAttorneyByAccounts();
            powerOfAttorneyByList.add(granteeId);
            granteeAccount.setPowerOfAttorneyByAccounts(powerOfAttorneyByList);
        }
        if (otp == null || otp.isEmpty()) {
            log.info("SENDING OTP...");
            notificationDomain.sendOtp(granteeAccount.getEmail(), granteeAccount.getMobileNumber());
            log.info("OTP SENT SUCCESSFULLY...");
            return new ServiceResponse("Otp Required", "421");
        } log.info("OTP SENT SUCCESSFULLY...");
        if (!notificationDomain.validateOtp(granteeAccount.getMobileNumber(), otp)){
            log.info("OTP DID NOT MATCH...");
            return new ServiceResponse("Otp did not match");
        }

        Account savedAccount = accountDomain.save(account);
        if (savedAccount == null){
            return new ServiceResponse("Could not add Power of Attorney");
        }
        Account savedGranteeAccount = accountDomain.save(account);
        if (savedGranteeAccount == null){
            return new ServiceResponse("Could not add Power of Attorney");
        }
        log.info("POWER OF ATTORNEY ADDED SUCCESSFULLY...");
        return new ServiceResponse("Power of attorney added", objectMapper.writeValueAsString(savedAccount));
    }

    @Override
    @Transactional
    public ServiceResponse revokePowerOfAttorney(String mobileNumber, String granteeId, String otp) throws JsonProcessingException {
        Account account = accountDomain.findByMobileNumberForForPowerOfAttorney(mobileNumber);
        if (account == null){
            return new ServiceResponse("Could not find Account with this mobile number");
        }
        if (account.getPowerOfAttorneyForAccounts().contains(granteeId)){
            account.getPowerOfAttorneyForAccounts().remove(granteeId);
        }
        else {
            log.info("THERE WAS NO ACCOUNT WITH THIS ID: {}", granteeId);
            return new ServiceResponse("Could not revoke Power of Attorney");
        }

        Account granteeAccount = accountDomain.findById(granteeId);
        if (granteeAccount == null){
            return new ServiceResponse("Could not find account ");
        }
        if (granteeAccount.getPowerOfAttorneyByAccounts().contains(account.getId())){
            granteeAccount.getPowerOfAttorneyByAccounts().remove(account.getId());
        }
        else {
            log.info("THERE WAS NO ACCOUNT WITH THIS ID: {}", account.getId());
            return new ServiceResponse("Could not revoke Power of Attorney");
        }
        if (otp == null || otp.isEmpty()) {
            log.info("SENDING OTP...");
            notificationDomain.sendOtp(account.getEmail(), account.getMobileNumber());
            log.info("OTP SENT SUCCESSFULLY...");
            return new ServiceResponse("Otp Required", "421");
        }
        if (!notificationDomain.validateOtp(account.getMobileNumber(), otp)){
            log.info("OTP DID NOT MATCH...");
            return new ServiceResponse("Otp did not match");
        }
        Account savedAccount = accountDomain.save(account);
        if (savedAccount == null){
            return new ServiceResponse("Could not add Power of Attorney");
        }
        Account savedGranteeAccount = accountDomain.save(granteeAccount);
        if (savedGranteeAccount == null){
            return new ServiceResponse("Could not add Power of Attorney");
        }
        log.info("POWER OF ATTORNEY REVOKED SUCCESSFULLY...");
        return new ServiceResponse("Power of attorney added", objectMapper.writeValueAsString(savedAccount));
    }

//    @Override
//    public ServiceResponse getInvestor(Map<String, String> param) throws JsonProcessingException {
//        Account account = accountDomain.find
//    }
}
