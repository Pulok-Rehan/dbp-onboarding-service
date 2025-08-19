package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "JointAccountEntity")
public class JointAccountEntity {
    @Id
    private String id;
    private String name;
    private String mobileNumbr;
    private String email;
    private String address;
    private String jointAccountPhoto;
    private String jointAccountSignature;
    private String jointAccountNidFront;
    private String jointAccountNidBack;

}
