package com.bracepl.dbp_onboarding_service.domain.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RmModel {
    private String id;
    private String name;
    private String emolyeeCode;
}
