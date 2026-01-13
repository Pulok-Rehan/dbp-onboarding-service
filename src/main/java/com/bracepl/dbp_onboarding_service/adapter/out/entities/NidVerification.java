package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class NidVerification {
    private String id;
    private String channel;
    private String nidNumber;
    private String faceSimilarity;
    private String message;
    private boolean success;
    private String nidFront;
    private String photo;
    private String boId;
    private String investorCode;
}
