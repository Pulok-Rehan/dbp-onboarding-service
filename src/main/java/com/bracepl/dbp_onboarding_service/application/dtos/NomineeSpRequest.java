package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NomineeSpRequest {

    private Long accountId;
    private String relationWith;
    private Double sharePercent;
    private String nomineeName;
    private String nomDob;
    private String fatherName;
    private String guardName;
    private String guardDob;
    private String gFatherName;
    private String gMotherName;
    private String guardNid;
    private String guardPassNo;
    private String createdBy;
    private String actionType;
    private String nomNid;
    private String nomPassportNo;
    private String motherName;
    private String isGuardian;
}
