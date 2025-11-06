package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.AccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.AccountRepositoryCustom;
import com.bracepl.dbp_onboarding_service.adapter.out.models.AccountSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class AccountRepositoryImpl implements AccountRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public AccountRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<AccountEntity> searchAccounts(AccountSearchRequest request) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Date range filter
        if (request.getFromDate() != null && request.getToDate() != null) {
            LocalDate from = LocalDate.parse(request.getFromDate(), formatter);
            LocalDate to = LocalDate.parse(request.getToDate(), formatter);
            criteria = criteria.and("createdAt")
                    .gte(from.atStartOfDay())
                    .lte(to.atTime(23, 59, 59));
        }

        // Status filter
        if (request.getStatus() != null) {
            criteria = criteria.and("accountStatus").is(request.getStatus());
        }

        // Investor Code filter
        if (request.getInvestorCode() != null && !request.getInvestorCode().isEmpty()) {
            criteria = criteria.and("investorCode").is(request.getInvestorCode());
        }

        // Mobile filter
        if (request.getMobileNumber() != null && !request.getMobileNumber().isEmpty()) {
            criteria = criteria.and("mobileNumber").is(request.getMobileNumber());
        }

        query.addCriteria(criteria);
        return mongoTemplate.find(query, AccountEntity.class);
    }
}
