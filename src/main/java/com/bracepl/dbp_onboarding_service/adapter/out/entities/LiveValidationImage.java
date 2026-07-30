package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LiveValidationImage {
    private String id;
    private String mobileNumber;
    private String photo;
    private String photoTiltingLeft;
    private String photoTiltingRight;
    private String photoSmiling;
    private String photoBlinking;
}
