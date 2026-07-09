package com.example.chat.infrastructure.keycloak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class KeycloakService {

    private final RestClient restClient;

    @Value("${keycloak.url}")
    private String url;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    public KeycloakService(RestClient restClient) {
        this.restClient = restClient;
    }

    public String getAdminToken() {

        AdminTokenResponse response = restClient.post()
                .uri(url + "/realms/master/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(
                        "grant_type=password" +
                                "&client_id=admin-cli" +
                                "&username=" + adminUsername +
                                "&password=" + adminPassword
                )
                .retrieve()
                .body(AdminTokenResponse.class);

        return response.access_token();
    }

    public void createUser(String username, String password) {

        String token = getAdminToken();

        KeycloakUser user = new KeycloakUser(
                username,
                true,
                java.util.List.of(
                        new KeycloakUser.Credential(
                                "password",
                                password,
                                false
                        )
                )
        );

        restClient.post()
                .uri(url + "/admin/realms/" + realm + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(user)
                .retrieve()
                .toBodilessEntity();
    }
}
