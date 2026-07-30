package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "platform_profiles")
public class PlatformProfile {
    @Id
    private String id;
    private String mobileNumber;
    private String name;
    private String email;
    private String image;
    private boolean isInvestor;
}
