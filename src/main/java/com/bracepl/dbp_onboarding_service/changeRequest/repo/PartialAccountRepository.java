//package com.bracepl.dbp_onboarding_service.changeRequest.repo;
//
//import com.bracepl.dbp_onboarding_service.adapter.out.entities.ParitalAccountEntity;
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface PartialAccountRepository extends MongoRepository<ParitalAccountEntity, String> {
//
//    Optional<ParitalAccountEntity> findByMobileNumber(String mobileNumber);
//
//    Optional<ParitalAccountEntity> findByEmailAddress(String emailAddress);
//
//    Optional<ParitalAccountEntity> findByMobileNumberAndHasChangeRequest(String mobileNumber, boolean hasChangeRequest);
//
//    Optional<ParitalAccountEntity> findByActiveChangeRequestId(String changeRequestId);
//
//    boolean existsByMobileNumber(String mobileNumber);
//}