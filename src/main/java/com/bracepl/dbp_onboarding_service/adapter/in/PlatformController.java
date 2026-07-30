package com.bracepl.dbp_onboarding_service.adapter.in;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.PlatformProfile;
import com.bracepl.dbp_onboarding_service.adapter.out.services.PlatformService;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/platform")
@AllArgsConstructor
public class PlatformController {
    private final PlatformService service;
    private final ObjectMapper objectMapper;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResponse create(
            @RequestParam String mobileNumber,
            @RequestParam String email,
            @RequestParam boolean isInvestor,
            @RequestPart(required = false) MultipartFile imageFile)
            throws IOException {

        PlatformProfile profile = PlatformProfile.builder()
                .mobileNumber(mobileNumber)
                .email(email)
                .isInvestor(isInvestor)
                .build();

        PlatformProfile platformProfile = service.create(profile, imageFile);

        return new ServiceResponse(
                "Platform profile created successfully",
                objectMapper.writeValueAsString(platformProfile)
        );
    }

    @GetMapping
    public ServiceResponse getAll() throws JsonProcessingException {
        return new ServiceResponse("Platform profiles retrieved successfully", objectMapper.writeValueAsString(service.getAll()));
    }


    @GetMapping("/mobile/{mobileNumber}")
    public ServiceResponse getByMobile(@PathVariable String mobileNumber) throws JsonProcessingException {
        return new ServiceResponse("Platform profile retrieved successfully", objectMapper.writeValueAsString(service.getByMobile(mobileNumber)));
    }

    @PutMapping(value = "/{mobileNumber}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResponse update(
            @PathVariable String mobileNumber,
            @RequestParam(required = false) String email,
            @RequestPart(required = false) MultipartFile imageFile) throws IOException {

        PlatformProfile profile = PlatformProfile.builder()
                .email(email)
                .build();

        return new ServiceResponse(
                "Platform profile updated successfully",
                objectMapper.writeValueAsString(
                        service.update(mobileNumber, profile, imageFile)
                )
        );
    }

}
