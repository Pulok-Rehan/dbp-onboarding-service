package com.bracepl.dbp_onboarding_service.domain.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EkycResponse {
    private int code;
    private String fullName;
    private String status;
    private String faceSimilarity;
}
