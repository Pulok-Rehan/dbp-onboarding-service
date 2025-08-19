package com.bracepl.dbp_onboarding_service.adapter.out.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NidVerificationResponse {
    private boolean success;
    private String message;
    private String fullName;
//    private String DOB;
//    private String NID;
    private String faceSimilarity;
    private int count;
}
