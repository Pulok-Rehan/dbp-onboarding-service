package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.ParitalAccountEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PartialAccountRepositoryCustom implements com.bracepl.dbp_onboarding_service.adapter.out.interfaces.PartialAccountRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    public PartialAccountRepositoryCustom(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<ParitalAccountEntity> searchAccounts(String request) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (request != null) {
            criteria = criteria.and("accountStatus").is(request);
        }
        query.addCriteria(criteria);
        return mongoTemplate.find(query, ParitalAccountEntity.class);
    }
}
