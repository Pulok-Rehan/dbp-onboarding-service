package com.bracepl.dbp_onboarding_service.adapter.out.wrapper;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MultipartInputResource extends ByteArrayResource {
    private final String filename;

    public MultipartInputResource(MultipartFile multipartFile) throws IOException {
        super(multipartFile.getBytes());
        this.filename = multipartFile.getOriginalFilename();
    }

    @Override
    public String getFilename() {
        return this.filename;
    }
}
