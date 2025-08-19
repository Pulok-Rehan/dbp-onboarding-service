package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "database_sequences")
public class DatabaseSequence {
    @Id
    private String id; // e.g., "investor_code"
    private long seq;
}
