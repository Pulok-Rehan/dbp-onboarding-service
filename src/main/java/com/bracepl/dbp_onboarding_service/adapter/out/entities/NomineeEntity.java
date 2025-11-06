package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@Table(name = "NomineeDetails")
@Document(collection = "Nominee")
public class NomineeEntity {
    @Id
    private String id;

    private String name;
    private String relation;
    private String nid;
    private LocalDate dateOfBirth;
    private double percentage;
    private String city;
    private String country;
    private String state;
    private String zipCode;
    private String address;
    private String mobileNumber;
}
