package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
public class InternalUser{
    private String name;
    private String employeeCode;
    private List<String> roles;
    private String designation;
    private String emailAddress;
    private String mobileNumber;
}
