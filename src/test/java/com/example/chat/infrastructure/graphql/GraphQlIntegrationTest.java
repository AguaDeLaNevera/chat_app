package com.example.chat.infrastructure.graphql;

import com.example.chat.infrastructure.keycloak.KeycloakService;
import com.example.chat.infrastructure.mongo.message.MessageMongoRepository;
import com.example.chat.infrastructure.mongo.user.UserMongoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("mongo")
class GraphQlIntegrationTest {

    @Autowired
    private UserMongoRepository userMongoRepository;

    @Autowired
    private MessageMongoRepository messageMongoRepository;

    @Value("${local.server.port}")
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeEach
    void setUp() {
        messageMongoRepository.deleteAll();
        userMongoRepository.deleteAll();
    }

    @Test
    void registerMutationCreatesUser() throws Exception {
        JsonNode response = postGraphql("""
                mutation {
                  register(username: "riki", password: "1234") {
                    id
                    username
                  }
                }
                """, null);

        assertTrue(response.path("errors").isMissingNode());
        assertEquals("riki", response.at("/data/register/username").asText());
        assertTrue(response.at("/data/register/id").isInt());
    }

    @Test
    void sendMessageMutationPersistsMessageForAuthenticatedUser() throws Exception {
        registerUser();

        JsonNode response = postGraphql("""
                mutation {
                  sendMessage(content: "hello from graphql") {
                    id
                    userId
                    content
                    sentAt
                  }
                }
                """, "test-token");

        assertTrue(response.path("errors").isMissingNode());
        assertEquals("hello from graphql", response.at("/data/sendMessage/content").asText());
        assertTrue(response.at("/data/sendMessage/userId").isInt());
        assertTrue(response.at("/data/sendMessage/sentAt").isTextual());
    }

    @Test
    void messagesQueryReturnsSavedMessageWithUsername() throws Exception {
        registerUser();

        postGraphql("""
                mutation {
                  sendMessage(content: "hello from graphql") {
                    id
                    userId
                    content
                    sentAt
                  }
                }
                """, "test-token");

        JsonNode response = postGraphql("""
                query {
                  messages {
                    id
                    userId
                    username
                    content
                  }
                }
                """, null);

        assertTrue(response.path("errors").isMissingNode());
        assertEquals(1, response.at("/data/messages").size());
        assertEquals("riki", response.at("/data/messages/0/username").asText());
        assertEquals("hello from graphql", response.at("/data/messages/0/content").asText());
        assertTrue(response.at("/data/messages/0/userId").isInt());
    }

    @Test
    void registerDuplicateUserReturnsGraphQlError() throws Exception {
        registerUser();

        JsonNode response = postGraphql("""
                mutation {
                  register(username: "riki", password: "1234") {
                    id
                    username
                  }
                }
                """, null);

        assertTrue(response.has("errors"));
        assertFalse(response.at("/errors/0/message").asText().isBlank());
        assertEquals("Username already exists", response.at("/errors/0/message").asText());
    }

    private void registerUser() throws Exception {
        JsonNode response = postGraphql("""
                mutation {
                  register(username: "riki", password: "1234") {
                    id
                    username
                  }
                }
                """, null);

        assertTrue(response.path("errors").isMissingNode());
        assertEquals("riki", response.at("/data/register/username").asText());
    }

    private JsonNode postGraphql(String query, String token) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("query", query));

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/graphql"))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(body));

        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }

        HttpResponse<String> response = httpClient.send(
                builder.build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode());
        return objectMapper.readTree(response.body());
    }

    @TestConfiguration
    static class TestBeans {
        @Bean
        @Primary
        KeycloakService keycloakService() {
            return new KeycloakService(null) {
                @Override
                public void createUser(String username, String password) {
                }
            };
        }

        @Bean
        JwtDecoder jwtDecoder() {
            return token -> Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .claim("preferred_username", "riki")
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .build();
        }
    }
}
