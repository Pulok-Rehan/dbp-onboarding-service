package com.bracepl.dbp_onboarding_service.domain.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JointAccoint {
    private String jointAccountname;
    private String jointAccountMobileNumbr;
    private String jointAccountEmail;
    private String jointAccountAddress;
    private String jointAccountPhoto;
    private String jointAccountSignature;
    private String jointAccountNidFront;
    private String jointAccountNidBack;
}
