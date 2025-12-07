package com.bracepl.dbp_onboarding_service.changeRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectionChangeRequest {
    private AccountSection section;
    private List<FieldChangeDetail> fields;
    private boolean completed;
}