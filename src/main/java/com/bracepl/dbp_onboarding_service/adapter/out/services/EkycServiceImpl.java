package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.EkycService;
import com.bracepl.dbp_onboarding_service.adapter.out.wrapper.MultipartInputResource;
import com.bracepl.dbp_onboarding_service.domain.models.Ekyc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
public class EkycServiceImpl implements EkycService {
    @Value("${ekyc.service.endpoint}")
    private String apiEndPoint;
    @Override
    public Ekyc callEkyc(MultipartFile nidFront, MultipartFile nidBack, String mobileNumber) throws IOException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("nid-front", new MultipartInputResource(nidFront));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Ekyc> response = restTemplate.postForEntity(apiEndPoint, requestEntity, Ekyc.class);
            return response.getBody();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
