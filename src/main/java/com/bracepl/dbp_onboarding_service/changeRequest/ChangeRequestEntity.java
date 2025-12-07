package com.bracepl.dbp_onboarding_service.changeRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "changeRequests")
public class ChangeRequestEntity {

    @Id
    private String id;

    private String accountId;
    private String partialAccountId;
    private String requestedBy;
    private String requestedFor;

    private ChangeRequestStatus status;

    private List<SectionChangeRequest> sectionChanges;

    private String adminRemarks;
    private String userRemarks;

    @CreatedDate
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;

    private boolean userNotified;
    private LocalDateTime notifiedAt;
}






