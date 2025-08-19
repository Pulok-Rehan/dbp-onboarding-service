package com.bracepl.dbp_onboarding_service.application.dtos;

import com.bracepl.dbp_onboarding_service.domain.enums.Action;
import lombok.Data;

@Data
public class RmAction {
    private Action action;
    private String reason;
    private String clientId;
}
