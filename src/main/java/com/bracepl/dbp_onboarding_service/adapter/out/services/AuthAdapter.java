package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.AccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.FailedLoginAttempt;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.InternalUser;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.UserCredentials;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.AccountRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.FailedLoginAttemptRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.InternalUserRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.UserCredentialRepository;
import com.bracepl.dbp_onboarding_service.domain.models.AuthResponse;
import com.bracepl.dbp_onboarding_service.application.dtos.LoginDto;
import com.bracepl.dbp_onboarding_service.application.dtos.RegisterDto;
import com.bracepl.dbp_onboarding_service.domain.interfaces.AuthDomain;
import com.bracepl.dbp_onboarding_service.domain.models.UserCredential;
import com.bracepl.dbp_onboarding_service.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
public class AuthAdapter implements AuthDomain {
    @Value("${keycloak.url}")
    private String keycloakUrl;
    @Value("${keycloak.realm}")
    private String keycloakRealm;
    @Value("${keycloak.tokenUrl}")
    private String keycloakTokenUrl;
    @Value("${keycloak.clientId}")
    private String keycloakClientId;
    @Value("${keycloak.clientSecret}")
    private String keycloakClientSecret;
    @Value("${keycloak.admin.username}")
    private String adminUserName;
    @Value("${keycloak.admin.password}")
    private String adminPassword;

    private final UserCredentialRepository userCredentialRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final FailedLoginAttemptRepository failedLoginAttemptRepository;
    private final InternalUserRepository internalUserRepository;
    private final AccountRepository accountRepository;
    private final WebClient webClient = WebClient.create();

    public AuthAdapter(UserCredentialRepository userCredentialRepository, RestTemplate restTemplate, ObjectMapper objectMapper, FailedLoginAttemptRepository failedLoginAttemptRepository, InternalUserRepository internalUserRepository, AccountRepository accountRepository) {
        this.userCredentialRepository = userCredentialRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.failedLoginAttemptRepository = failedLoginAttemptRepository;
        this.internalUserRepository = internalUserRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public UserCredential findByEmail(String email) {
        try {
            Optional<UserCredentials> optionalUserCredentials = userCredentialRepository.findByEmail(email);
            UserCredential userCredential;
            if (optionalUserCredentials.isPresent()){
                userCredential = this.populateToUserCredsObject(optionalUserCredentials.get());
                return userCredential;
            }
            return null;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UserCredential registerNewUser(String email, String mobileNo, String tempPassword, LocalDateTime expiryTime) {
        try {
            UserCredentials userCredentials = UserCredentials.builder()
                    .email(email)
                    .mobileNo(mobileNo)
                    .tempPassword(tempPassword)
                    .tempPassExpiration(expiryTime)
                    .isTempPassActive(true)
                    .build();
            userCredentialRepository.save(userCredentials);
            return this.populateToUserCredsObject(userCredentials);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UserCredential findByMobileNumber(String mobileNumber) {
        try {
            Optional<UserCredentials> optionalUserCredentials = userCredentialRepository.findByMobileNo(mobileNumber);
            UserCredential userCredential;
            if (optionalUserCredentials.isPresent()){
                userCredential = this.populateToUserCredsObject(optionalUserCredentials.get());
                return userCredential;
            }
            return null;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateTempPasswordStatus(String id) throws Exception {
        try {
            Optional<UserCredentials> optionalUserCredentials = userCredentialRepository.findById(id);
            if (optionalUserCredentials.isPresent()){
                optionalUserCredentials.get().setTempPassActive(false);
                userCredentialRepository.save(optionalUserCredentials.get());
                return true;
            }
            return false;
        }
        catch (Exception e){
            e.printStackTrace();
            throw new Exception("COULD NOT UPDATE PASSWORD");
        }

    }

    @Override
    public String getAdminAccessTokenFromKeycloak() throws JsonProcessingException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "password");
            form.add("client_id", keycloakClientId);
            form.add("username", adminUserName);
            form.add("password", adminPassword);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    keycloakUrl + "/realms/master/protocol/openid-connect/token",
                    entity,
                    String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String findUserIdByUserNameFromKeycloak(String accessToken, String userName) throws JsonProcessingException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    keycloakUrl + "/admin/realms/" + keycloakRealm + "/users?username=" + userName,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JsonNode result = objectMapper.readTree(response.getBody());
            return result.get(0).get("id").asText();
            //needs to check
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String findUserIdByEmailFromKeycloak(String accessToken, String email) throws JsonProcessingException {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
//                headers.setAccept(List.of(MediaType.APPLICATION_JSON));

                HttpEntity<Void> entity = new HttpEntity<>(headers);
                String url = keycloakUrl + "/admin/realms/" + keycloakRealm
                        + "/users?email=" + email;


                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

                JsonNode result = objectMapper.readTree(response.getBody());
                System.out.println("Search result: " + result);

                for (JsonNode user : result) {
                    if (email.equalsIgnoreCase(user.get("email").asText(""))) {
                        return user.get("id").asText();
                    }
                }


                return "";  // No match found
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }}

    @Override
    public String resetPasswordInKeycloak(String password, String userId, boolean isFirstTime, String accessToken) throws JsonProcessingException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            Map<String, Object> passwordPayload = new HashMap<>();
            passwordPayload.put("type", "password");
            passwordPayload.put("value", password);
            passwordPayload.put("temporary", isFirstTime);

            HttpEntity<Map<String, Object>> passEntity = new HttpEntity<>(passwordPayload, headers);
            restTemplate.put(
                    keycloakUrl + "/admin/realms/" + keycloakRealm + "/users/" + userId + "/reset-password",
                    passEntity
            );
            return "Password Reset Successful";
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public AuthResponse login(LoginDto loginDto) {
        String username = loginDto.getUsername();
        Optional<FailedLoginAttempt> attemptOptional = failedLoginAttemptRepository.findByUsername(username);
        if (attemptOptional.isPresent()) {
            FailedLoginAttempt attempt = attemptOptional.get();
            if (attempt.getAttempts() >= 3) {
                Duration lockDuration = Duration.between(attempt.getLastFailedAt(), LocalDateTime.now());
                if (lockDuration.toHours() < 1) {
                    return AuthResponse.builder()
                            .statusCode("423")
                            .build();
                } else {
                    failedLoginAttemptRepository.deleteById(username);
                }
            }
        }

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", keycloakClientId);
        requestBody.add("client_secret", keycloakClientSecret); // ✅ Ensure this is a method call
        requestBody.add("username", loginDto.getUsername());
        requestBody.add("password", loginDto.getPassword());
        requestBody.add("grant_type", "password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<AuthResponse> response = restTemplate.exchange(
                    keycloakUrl+keycloakTokenUrl,
                    HttpMethod.POST,
                    entity,
                    AuthResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> claims = JwtUtils.decodeJWT(response.getBody().getAccessToken());
                if (attemptOptional.isPresent()){
                    attemptOptional.get().setAttempts(0);
                    failedLoginAttemptRepository.save(attemptOptional.get());
                }
                log.info("FOUND 200 FROM KEYCLOAK");
//                Optional<AccountEntity> accountEntityOptional = accountRepository.findByMobileOrEmailOrInvestorCode(loginDto.getUsername());
//                if (accountEntityOptional.isEmpty()){
//                    return null;
//                }
                return AuthResponse.builder()
                        .statusCode("200")
                        .accessToken(response.getBody().getAccessToken())
                        .emailAddress((String) claims.get("email"))
                        .mobileNumber((String) claims.get("preferred_username"))
                        .refreshToken(response.getBody().getRefreshToken()).build();
            }

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401){
                FailedLoginAttempt failedLogin = attemptOptional.orElseGet(() -> {
                    FailedLoginAttempt f = new FailedLoginAttempt();
                    f.setUsername(username);
                    f.setAttempts(0);
                    return f;
                });

                failedLogin.setAttempts(failedLogin.getAttempts() + 1);
                failedLogin.setLastFailedAt(LocalDateTime.now());
                failedLoginAttemptRepository.save(failedLogin);
                log.info("FOUND 401 FROM KEYCLOAK");
                return AuthResponse.builder()
                        .statusCode("401").build();
            }
            if (e.getStatusCode().value() == 400){
                log.info("FOUND 400 FROM KEYCLOAK HENCE TEMPORARY PASSWORD NEEDS TO BE CHANGED...");
                return AuthResponse.builder()
                        .statusCode("421").build();
            }
            log.info("Client error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return null;
        } catch (RestClientException e) {
            log.info("FOUND EXCEPTION FROM KEYCLOAK");
            log.info("Error requesting token: " + e.getMessage());
            return null;
        }

        return null;
    }

    @Override
    public String setNewPassword(String userId, String newPassword, String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Step 1: Set the password as permanent
            Map<String, Object> passwordPayload = new HashMap<>();
            passwordPayload.put("type", "password");
            passwordPayload.put("value", newPassword);
            passwordPayload.put("temporary", false);

            HttpEntity<Map<String, Object>> passwordRequest = new HttpEntity<>(passwordPayload, headers);
            restTemplate.exchange(
                    keycloakUrl + "/admin/realms/" + keycloakRealm + "/users/" + userId + "/reset-password",
                    HttpMethod.PUT,
                    passwordRequest,
                    Void.class
            );

            // Step 2: Clear required actions
            Map<String, Object> clearActions = new HashMap<>();
            clearActions.put("requiredActions", List.of()); // empty list

            HttpEntity<Map<String, Object>> updateUser = new HttpEntity<>(clearActions, headers);
            restTemplate.exchange(
                    keycloakUrl + "/admin/realms/" + keycloakRealm + "/users/" + userId,
                    HttpMethod.PUT,
                    updateUser,
                    Void.class
            );
            return "Password Changed successfully";
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String forgotPassword(String userId, String password, String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Step 1: Set the password as permanent
            Map<String, Object> passwordPayload = new HashMap<>();
            passwordPayload.put("type", "password");
            passwordPayload.put("value", password);
            passwordPayload.put("temporary", true);

            HttpEntity<Map<String, Object>> passwordRequest = new HttpEntity<>(passwordPayload, headers);
            restTemplate.exchange(
                    keycloakUrl + "/admin/realms/" + keycloakRealm + "/users/" + userId + "/reset-password",
                    HttpMethod.PUT,
                    passwordRequest,
                    Void.class
            );
            return "Temporary password sent";
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public InternalUser findByEmployeeCode(String employeeCode) {
        try {
            Optional<InternalUser> internalUserOptional = internalUserRepository.findByEmployeeCode(employeeCode);
            if (internalUserOptional.isEmpty()){
                return null;
            }
            return internalUserOptional.get();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            return Boolean.TRUE.equals(webClient.post()
                    .uri(keycloakUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token/introspect")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .bodyValue("token=" + token +
                                    "&client_id=" + keycloakClientId+
                            "&client_secret=" + keycloakClientSecret)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(body -> (Boolean) body.get("active"))
                    .block());
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }


    @Override
    public String registerUserInKeycloak(RegisterDto registerDto, String accessToken, String password, List<String> roles) throws JsonProcessingException {
        try {
            List<String> roleList = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> user = new HashMap<>();
            user.put("username", registerDto.getMobileNumber());
            user.put("email", registerDto.getEmail());
            user.put("firstName", "NA");
            user.put("lastName", "NA");
            user.put("emailVerified", true);
            user.put("requiredActions", List.of("UPDATE_PASSWORD"));
            user.put("enabled", true);

            HttpEntity<Map<String, Object>> userEntity = new HttpEntity<>(user, headers);
            ResponseEntity<Void> createUserResponse = restTemplate.postForEntity(
                    keycloakUrl + "/admin/realms/" + keycloakRealm + "/users",
                    userEntity,
                    Void.class
            );

            if (!createUserResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to create user");
            }
            String userId = this.findUserIdByUserNameFromKeycloak(accessToken, registerDto.getMobileNumber());
            if (userId.isEmpty()){
                return "";
            }
            String resetSuccessful = this.resetPasswordInKeycloak(password, userId, true, accessToken);
            if (resetSuccessful.isEmpty()){
                return "";
            }
            if (roles.isEmpty()){
                roleList.add("USER");
                assignRoleInKeycloak(accessToken, userId, roleList);
            }
            assignRoleInKeycloak(accessToken, userId, roles);
            return "User Registration Successfull";
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void assignRoleInKeycloak(String token, String userId, List<String> roleNames) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Map<String, Object>> roleRepresentations = new ArrayList<>();

        for (String roleName : roleNames) {
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> roleResponse = restTemplate.exchange(
                    keycloakUrl + "/admin/realms/" + keycloakRealm + "/roles/" + roleName,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (roleResponse.getStatusCode().is2xxSuccessful() && roleResponse.getBody() != null) {
                roleRepresentations.add(roleResponse.getBody());
            }
        }

        if (!roleRepresentations.isEmpty()) {
            HttpEntity<List<Map<String, Object>>> assignEntity = new HttpEntity<>(roleRepresentations, headers);
            restTemplate.postForEntity(
                    keycloakUrl + "/admin/realms/" + keycloakRealm + "/users/" + userId + "/role-mappings/realm",
                    assignEntity,
                    Void.class
            );
        }
    }

    private UserCredential populateToUserCredsObject(UserCredentials userCredentials){
        return UserCredential.builder()
                .id(userCredentials.getId())
                .email(userCredentials.getEmail())
                .mobileNo(userCredentials.getMobileNo())
                .tempPassword(userCredentials.getTempPassword())
                .tempPassExpiration(userCredentials.getTempPassExpiration())
                .isTempPassActive(userCredentials.isTempPassActive())
                .build();
    }
}
