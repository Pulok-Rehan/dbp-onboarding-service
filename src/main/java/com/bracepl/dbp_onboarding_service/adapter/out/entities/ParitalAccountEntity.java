package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "partialAccountDetails")
public class ParitalAccountEntity {
    @Id
    private String id;
    private String investorCode;
    private String emailAddress;
    private String mobileNumber;
    private String name;
    private String gender;
    private String nid;
    private String fathersName;
    private String mothersName;
    private String dateOfBirth;
    private String addressLine1;
    private String city;
    private String country;
    private String state;
    private String zipCode;
    private BankEntity bank;
    private String accountNo;
    private String residency;
    private String boType;
    private String nidFront;
    private String nidBack;
    private String photo;
    private String signature;
    private String chequeLeaf;
    private boolean boLinked;
    private JointAccountEntity jointAccountEntity;
    @DBRef
    private List<NomineeEntity> nominees;
    private CompletionSectionEntity completionSection;
    private boolean isActive;
    private String accountStatus;
    private String transactionStatus;
    private String csdId;
    private String rm;
    private String preferedBranch;
    private boolean csdContacted;
    private String remark;
}
