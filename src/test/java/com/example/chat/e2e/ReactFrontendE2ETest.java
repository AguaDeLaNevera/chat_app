package com.example.chat.e2e;

import com.example.chat.infrastructure.keycloak.KeycloakService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("mongo")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReactFrontendE2ETest {

    private static final String KEYCLOAK_TOKEN_PATH = "/realms/chat/protocol/openid-connect/token";
    private static final String MONGO_IMAGE = "mongo:7.0.14";

    @Container
    static final MongoDBContainer mongo = new MongoDBContainer(MONGO_IMAGE);

    @Value("${local.server.port}")
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    private int keycloakPort;

    private static HttpServer keycloakServer;

    @DynamicPropertySource
    static void registerMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @BeforeAll
    void startKeycloakStub() throws IOException {
        keycloakServer = HttpServer.create(new InetSocketAddress(0), 0);
        keycloakServer.createContext(KEYCLOAK_TOKEN_PATH, this::handleTokenRequest);
        keycloakServer.start();
        keycloakPort = keycloakServer.getAddress().getPort();
    }

    @AfterAll
    void stopKeycloakStub() {
        if (keycloakServer != null) {
            keycloakServer.stop(0);
        }
    }

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver(chromeOptions());
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void userCanRegisterLoginSendMessageAndLogOut() {
        String username = "e2e-" + UUID.randomUUID().toString().substring(0, 8);
        String password = "secret123";
        String message = "hello from the browser";

        open("/register.html");
        fill("username", username);
        fill("password", password);
        clickButton("Register");

        wait.until(ExpectedConditions.urlContains("/react/login.html"));

        injectKeycloakTokenUrl();
        fill("username", username);
        fill("password", password);
        clickButton("Login");

        wait.until(ExpectedConditions.urlContains("/react/index.html"));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".chat-shell")));

        assertTrue(driver.findElements(By.xpath("//button[normalize-space()='Register']")).isEmpty());
        assertEquals(1, driver.findElements(By.xpath("//button[normalize-space()='Logout']")).size());

        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.id("message-input")));
        input.sendKeys(message);
        clickButton("Send");

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".messages"), message));
        assertTrue(driver.findElement(By.cssSelector(".messages")).getText().contains(message));

        clickButton("Logout");
        wait.until(ExpectedConditions.urlContains("/react/login.html"));
    }

    @Test
    void incorrectCredentialsOnLogin() {
        open("/login.html");
        fill("username", "wrong");
        fill("password", "wrong");
        clickButton("Login");
        wait.until(ExpectedConditions.urlContains("/react/login.html"));

        WebElement error = wait.until(driver -> {
                    WebElement element = driver.findElement(By.className("error"));
                    String text = element.getText();
                    return !text.isBlank() ? element : null;
                }
        );

        assertFalse(error.getText().isBlank());
    }

    private void open(String path) {
        driver.get("http://localhost:" + port + "/react" + path);
    }

    private void injectKeycloakTokenUrl() {
        String tokenUrl = "http://localhost:" + keycloakPort + KEYCLOAK_TOKEN_PATH;
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "window.ChatConfig = { keycloakTokenUrl: arguments[0] };",
                tokenUrl
        );
    }

    private void fill(String id, String value) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
        element.clear();
        element.sendKeys(value);
    }

    private void clickButton(String label) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='" + label + "']"))).click();
    }

    private void handleTokenRequest(HttpExchange exchange) throws IOException {
        try (exchange) {

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                addCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            addCorsHeaders(exchange);

            sendJsonToBrowser(exchange, """
                    {
                      "access_token": "%s",
                      "token_type": "Bearer",
                      "expires_in": 3600
                    }
                    """.formatted(createFakeJwtToken()));
        }
    }

    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json");
    }

    private void sendJsonToBrowser(HttpExchange exchange, String json) throws IOException {
        byte[] bytes = json.strip().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
        }
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

    private static String createFakeJwtToken() {
        String header = base64Url("""
                {"alg":"none","typ":"JWT"}
                """);
        String payload = base64Url("""
                {"preferred_username":"%s","sub":"%s"}
                """.formatted("riki", "riki"));
        return header + "." + payload + ".";
    }

    private static String base64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.strip().getBytes(StandardCharsets.UTF_8));
    }

    private ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1440,1200");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-first-run");
        options.addArguments("--no-default-browser-check");
        options.setBinary(findChromeBinary().toString());
        return options;
    }

    private Path findChromeBinary() {
        String programFiles = System.getenv("ProgramFiles");
        if (programFiles != null) {
            Path primary = Path.of(programFiles, "Google", "Chrome", "Application", "chrome.exe");
            if (Files.exists(primary)) {
                return primary;
            }
        }

        String programFilesX86 = System.getenv("ProgramFiles(x86)");
        if (programFilesX86 != null) {
            Path fallback = Path.of(programFilesX86, "Google", "Chrome", "Application", "chrome.exe");
            if (Files.exists(fallback)) {
                return fallback;
            }
        }

        throw new IllegalStateException("Could not find chrome.exe");
    }
}
