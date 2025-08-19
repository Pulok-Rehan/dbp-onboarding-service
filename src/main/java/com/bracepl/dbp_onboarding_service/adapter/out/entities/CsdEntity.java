package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document(collection = "csd_entity")
@Data
public class CsdEntity {
    @Id
    private String id;
    private String name;
    private String emolyeeCode;
}
