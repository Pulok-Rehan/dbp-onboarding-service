package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.User;

@Document(collection = "RmEntity")
@Data
public class RmEntity extends UserCredentials {
    private String name;
    private String employeeCode;
    private String mobileNumber;
}
