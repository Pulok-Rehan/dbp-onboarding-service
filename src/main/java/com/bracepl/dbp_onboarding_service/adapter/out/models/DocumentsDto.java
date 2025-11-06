package com.bracepl.dbp_onboarding_service.adapter.out.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class DocumentsDto {
    private MultipartFile boAttachment;
    private MultipartFile photo;
    private MultipartFile jointAccountPhoto;
    private MultipartFile jointAccountSignature;
    private MultipartFile jointAccountNidFront;
    private MultipartFile jointAccountNidBack;
    private MultipartFile signature;
    private MultipartFile chequeLeaf;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> fieldsToUpdate;
}
