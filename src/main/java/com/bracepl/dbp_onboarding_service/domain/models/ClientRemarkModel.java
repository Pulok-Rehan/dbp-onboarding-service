package com.bracepl.dbp_onboarding_service.domain.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientRemarkModel {
    private String csdId;
    private String accountId;
    private String remarks;
    private String accountStatus;
}
