package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ClientRemarks")
@Data
@Builder
public class ClientRemarks {
    @Id
    private String id;
    private String userId;
    private String accountId;
    private String remarks;
    private String rejectedReason;
}
