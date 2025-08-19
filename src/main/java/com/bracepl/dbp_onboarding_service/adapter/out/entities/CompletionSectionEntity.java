package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@Table(name = "completion_sections")
@Document(collection = "CompletionSection")
public class CompletionSectionEntity {
    @Id
    private String id;

    private String mobileNumber;
    private String email;

    private boolean personalDetails;
    private boolean address;
    private boolean nidPhotos;
    private boolean bankDetails;
    private boolean nomineeDetails;
    private boolean documents;
    private boolean isActive;
}
