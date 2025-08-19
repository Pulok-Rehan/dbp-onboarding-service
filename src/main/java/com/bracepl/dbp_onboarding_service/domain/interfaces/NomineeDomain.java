package com.bracepl.dbp_onboarding_service.domain.interfaces;

import com.bracepl.dbp_onboarding_service.domain.models.Nominee;

import java.util.List;

public interface NomineeDomain {
    String addNominee(Nominee nominees, String mobileNumber);
    Nominee getNominee(String nomineeId);
    List<Nominee> getNominees(String mobileNumber);
}
