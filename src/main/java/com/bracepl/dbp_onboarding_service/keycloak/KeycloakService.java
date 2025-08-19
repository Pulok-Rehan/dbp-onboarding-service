package com.bracepl.dbp_onboarding_service.keycloak;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Change these according to your Keycloak setup
    private final String keycloakUrl = "http://10.7.93.19:8090";
    private final String realm = "bracepl-dbp";
    private final String adminUsername = "admin";
    private final String adminPassword = "admin";
    private final String clientId = "auth-service";

    public void registerUser(SignupRequest request) throws Exception {
        String accessToken = getAdminAccessToken();

        // Step 1: Create user
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> user = new HashMap<>();
        user.put("username", request.getUsername());
        user.put("email", request.getEmail());
        user.put("firstName", request.getEmail());
        user.put("lastName", request.getEmail());
        user.put("enabled", true);

        HttpEntity<Map<String, Object>> userEntity = new HttpEntity<>(user, headers);
        ResponseEntity<Void> createUserResponse = restTemplate.postForEntity(
                keycloakUrl + "/admin/realms/" + realm + "/users",
                userEntity,
                Void.class
        );

        if (!createUserResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to create user");
        }

        // Step 2: Get user ID
        String userId = findUserIdByUsername(accessToken, request.getUsername());

        // Step 3: Set password
        Map<String, Object> passwordPayload = new HashMap<>();
        passwordPayload.put("type", "password");
        passwordPayload.put("value", request.getPassword());
        passwordPayload.put("temporary", true);

        HttpEntity<Map<String, Object>> passEntity = new HttpEntity<>(passwordPayload, headers);
        restTemplate.put(
                keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password",
                passEntity
        );
    }

    private String getAdminAccessToken() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("username", adminUsername);
        form.add("password", adminPassword);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                keycloakUrl + "/realms/master/protocol/openid-connect/token",
                entity,
                String.class
        );

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }


    private String findUserIdByUsername(String token, String username) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                keycloakUrl + "/admin/realms/" + realm + "/users?username=" + username,
                HttpMethod.GET,
                entity,
                String.class
        );

        JsonNode result = objectMapper.readTree(response.getBody());
        return result.get(0).get("id").asText();
    }
}
