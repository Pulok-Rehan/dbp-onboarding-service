package com.bracepl.dbp_onboarding_service.domain.interfaces;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.NidVerification;
import com.bracepl.dbp_onboarding_service.adapter.out.models.NidVerificationResponse;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.CompletionSection;
import com.bracepl.dbp_onboarding_service.domain.models.Ekyc;
import com.bracepl.dbp_onboarding_service.domain.models.JointAccoint;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AccountDomain {
    Account save(Account account);
    Account saveWithBoLinked(Account account, String boNumber);
    Account saveWithJointAccount(Account account, JointAccoint jointAccoint);
    boolean isDuplicateAccount(String nidNumber);
    Account findByInvestorCode(String investorCode);
    boolean findByMobileNumber(String mobileNumber);
    Account findById(String id);
//    Account findByEmail(String id);
    Account findByMobileNumberForForgetPassword(String mobileNumber);
    Account findByMobileNumberForForPowerOfAttorney(String mobileNumber);
    Account searchByMobileOrEmailOrInvestorCode(String input);
    Ekyc callEkycService(MultipartFile nidFront, MultipartFile nidBack, MultipartFile photo, String mobileNumber) throws IOException;
    NidVerification saveImagesForEkyc(MultipartFile nidFront, MultipartFile nidBack, MultipartFile photo, String investorCode, String boId, String nidNumber) throws IOException;
    NidVerificationResponse callNidVerification(String nidNumber, String dateOfBirth, MultipartFile photo, MultipartFile nidPhoto, String channel);
    CompletionSection getAccountCompletionRate(String mobileNumber);
}
