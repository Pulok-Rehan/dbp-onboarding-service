package com.bracepl.dbp_onboarding_service.changeRequest.repo;

import com.bracepl.dbp_onboarding_service.changeRequest.ChangeRequestEntity;
import com.bracepl.dbp_onboarding_service.changeRequest.ChangeRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChangeRequestRepository extends MongoRepository<ChangeRequestEntity, String> {
    
    Optional<ChangeRequestEntity> findByPartialAccountId(String partialAccountId);
    
    List<ChangeRequestEntity> findByAccountId(String accountId);
    
    Optional<ChangeRequestEntity> findByRequestedForAndStatus(String requestedFor, ChangeRequestStatus status);

    Page<ChangeRequestEntity> findByStatus(ChangeRequestStatus status, Pageable pageable);
    
    Page<ChangeRequestEntity> findAllByOrderByRequestedAtDesc(Pageable pageable);
    
    List<ChangeRequestEntity> findByRequestedFor(String requestedFor);
    
    boolean existsByPartialAccountIdAndStatus(String partialAccountId, ChangeRequestStatus status);
}

