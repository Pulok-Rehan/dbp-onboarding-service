package com.bracepl.dbp_onboarding_service.domain.models;

import lombok.Data;

@Data
public class ServiceResponse {
    private boolean hasError;
    private String message;
    private String content;

    public ServiceResponse(String message){
        this.hasError = true;
        this.message = message;
        this.content = null;
    }

    public ServiceResponse(String message, String content){
        this.hasError = false;
        this.message = message;
        this.content = content;
    }

    public ServiceResponse(String message, String content, boolean hasError){
        this.hasError = hasError;
        this.message = message;
        this.content = content;
    }

}
