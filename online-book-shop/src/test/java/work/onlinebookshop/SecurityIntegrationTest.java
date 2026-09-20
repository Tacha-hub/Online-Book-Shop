package work.onlinebookshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import work.onlinebookshop.model.Role.RoleName;
import work.onlinebookshop.model.User;
import work.onlinebookshop.repository.RoleRepository;
import work.onlinebookshop.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityIntegrationTest {
    @LocalServerPort
    private int port;
    @Autowired
    private UserRepository users;
    @Autowired
    private RoleRepository roles;
    @Autowired
    private PasswordEncoder encoder;

    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void registrationPersistsDetailsAndOnlyUserRole() throws Exception {
        String body = """
                {"email":"reader@example.com","password":"Reader2026!",
                 "repeatPassword":"Reader2026!","firstName":"Test","lastName":"Reader",
                 "shippingAddress":"Kyiv","roles":["ROLE_ADMIN"]}
                """;
        assertEquals(200, request("POST", "/auth/register", null, body).statusCode());
        User user = users.findByEmail("reader@example.com").orElseThrow();
        assertEquals("Test", user.getFirstName());
        assertEquals("Reader", user.getLastName());
        assertEquals("Kyiv", user.getShippingAddress());
        assertTrue(encoder.matches("Reader2026!", user.getPassword()));
        assertFalse(user.getPassword().equals("Reader2026!"));
        assertEquals(Set.of(RoleName.ROLE_USER), user.getRoles().stream()
                .map(role -> role.getName()).collect(java.util.stream.Collectors.toSet()));
        assertEquals(409, request("POST", "/auth/register", null, body).statusCode());
        assertEquals(200, request("GET", "/books", "reader@example.com:Reader2026!", null)
                .statusCode());
    }

    @Test
    void basicAuthenticationEnforcesRolesAndAccountStatus() throws Exception {
        createUser("user@example.com", RoleName.ROLE_USER, false);
        createUser("admin-only@example.com", RoleName.ROLE_ADMIN, false);
        createUser("disabled@example.com", RoleName.ROLE_USER, true);
        String reader = "user@example.com:Test2026!";
        String admin = "admin-only@example.com:Test2026!";
        String book = """
                {"title":"Security","author":"Author","isbn":"security-test-isbn","price":20}
                """;
        assertEquals(401, request("GET", "/books", null, null).statusCode());
        assertEquals(401, request("GET", "/books", "user@example.com:wrong", null).statusCode());
        assertEquals(401, request("GET", "/books", "disabled@example.com:Test2026!", null)
                .statusCode());
        assertEquals(200, request("GET", "/books", reader, null).statusCode());
        assertEquals(200, request("GET", "/books/search?title=Security", reader, null).statusCode());
        assertEquals(403, request("GET", "/books", admin, null).statusCode());
        assertEquals(403, request("POST", "/books", reader, book).statusCode());
        HttpResponse<String> created = request("POST", "/books", admin, book);
        assertEquals(201, created.statusCode(), created.body());
        var matcher = java.util.regex.Pattern.compile("\"id\"\\s*:\\s*(\\d+)")
                .matcher(created.body());
        assertTrue(matcher.find());
        String path = "/books/" + matcher.group(1);
        assertEquals(200, request("GET", path, reader, null).statusCode());
        assertEquals(403, request("PUT", path, reader, book).statusCode());
        assertEquals(403, request("DELETE", path, reader, null).statusCode());
        assertEquals(200, request("PUT", path, admin, book).statusCode());
        assertEquals(204, request("DELETE", path, admin, null).statusCode());
        assertEquals(404, request("GET", path, reader, null).statusCode());
    }

    @Test
    void swaggerAndSeededAdminAreAvailable() throws Exception {
        assertEquals(200, request("GET", "/swagger-ui/index.html", null, null).statusCode());
        assertEquals(200, request("GET", "/v3/api-docs", null, null).statusCode());
        assertEquals(200, request("GET", "/books", "admin@bookstore.local:LocalAdmin2026!", null)
                .statusCode());
        assertTrue(users.findAll().stream().allMatch(user ->
                !users.findByEmail(user.getEmail()).orElseThrow().getRoles().isEmpty()));
    }

    private void createUser(String email, RoleName role, boolean deleted) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode("Test2026!"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setDeleted(deleted);
        user.setRoles(new HashSet<>(Set.of(roles.findByName(role).orElseThrow())));
        users.save(user);
    }

    private HttpResponse<String> request(String method, String path, String credentials,
                                        String body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(
                URI.create("http://localhost:" + port + "/api" + path));
        if (credentials != null) {
            builder.header("Authorization", "Basic " + Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8)));
        }
        builder.header("Content-Type", "application/json");
        builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body));
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }
}
