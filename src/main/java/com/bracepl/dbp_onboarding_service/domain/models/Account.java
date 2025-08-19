package com.bracepl.dbp_onboarding_service.domain.models;

import com.bracepl.dbp_onboarding_service.domain.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String id;
    private String name;
    private String gender;
    private String nid;
    private String email;
    private String mobileNumber;
    private String fathersName;
    private String mothersName;
    private String dateOfBirth;
    private String investorCode;
    private String residency;
    private String boType;
    private boolean boLinked;
    private String boNumber;
    private String addressLine1;
    private String city;
    private String country;
    private String state;
    private String zipCode;
    private String bankName;
    private String branchName;
    private String routingNumber;
    private String accountNo;
    private String nidFront;
    private String nidBack;
    private String photo;
    private String signature;
    private String chequeLeaf;
    private String jointAccountname;
    private String jointAccountMobileNumbr;
    private String jointAccountEmail;
    private String jointAccountAddress;
    private String jointAccountPhoto;
    private String jointAccountSignature;
    private String jointAccountNidFront;
    private String jointAccountNidBack;
    private boolean active;
    private String transactionStatus;
    private boolean matched;
    private CompletionSection completionSection;
    private List<String> powerOfAttorneyForAccounts;
    private List<String> powerOfAttorneyByAccounts;
    private AccountStatus accountStatus;
    private String rmId;
    private String csdId;
    private String createdAt;
    private String updatedAt;
}
