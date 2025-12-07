package com.bracepl.dbp_onboarding_service.application.dtos.partialAccount;

import lombok.Data;

import java.util.List;

@Data
public class ParameterProperties {
    private boolean validated;
    private List<String> changeRequest;
    private String remark;
}
