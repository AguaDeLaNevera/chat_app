package com.example.chat.infrastructure.keycloak;

import java.util.List;

public record KeycloakUser(
        String username,
        boolean enabled,
        List<Credential> credentials
) {

    public record Credential(
            String type,
            String value,
            boolean temporary
    ) {}

}
