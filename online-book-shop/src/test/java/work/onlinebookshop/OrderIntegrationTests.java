package work.onlinebookshop;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import work.onlinebookshop.dto.user.UserRegistrationRequestDto;
import work.onlinebookshop.model.Book;
import work.onlinebookshop.repository.OrderRepository;
import work.onlinebookshop.repository.book.BookRepository;
import work.onlinebookshop.security.JwtUtil;
import work.onlinebookshop.service.UserService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderIntegrationTests {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    private int port;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String token;
    private Book firstBook;
    private Book secondBook;

    @BeforeEach
    void setUp() throws Exception {
        token = registerUser();
        firstBook = saveBook("10.25");
        secondBook = saveBook("4.50");
    }

    @Test
    void placeOrderCopiesAllItemsAndClearsCart() throws Exception {
        addBook(firstBook, 2);
        addBook(secondBook, 3);

        JsonNode order = placeOrder();
        long orderId = order.get("id").asLong();
        assertThat(order.get("status").asText()).isEqualTo("PENDING");
        assertThat(order.get("total").decimalValue()).isEqualByComparingTo("34.00");
        assertThat(order.get("orderItems").size()).isEqualTo(2);
        assertThat(order.get("orderDate").asText()).isNotBlank();
        assertThat(jdbcTemplate.queryForObject(
                "SELECT shipping_address FROM orders WHERE id = ?", String.class, orderId))
                .isEqualTo("Kyiv, Shevchenko ave, 1");
        assertThat(json(request("GET", "/cart", token, null), 200)
                .get("cartItems").isEmpty()).isTrue();

        firstBook.setPrice(new BigDecimal("99.00"));
        bookRepository.save(firstBook);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT price FROM order_items WHERE order_id = ? AND book_id = ?",
                BigDecimal.class, orderId, firstBook.getId()))
                .isEqualByComparingTo("10.25");
        JsonNode history = json(request("GET", "/orders", token, null), 200);
        assertThat(history.get("content").get(0).get("total").decimalValue())
                .isEqualByComparingTo("34.00");
    }

    @Test
    void historyAndItemsSupportPaginationAndSorting() throws Exception {
        addBook(firstBook, 1);
        addBook(secondBook, 1);
        long firstOrderId = placeOrder().get("id").asLong();
        addBook(firstBook, 1);
        long secondOrderId = placeOrder().get("id").asLong();

        JsonNode history = json(request("GET", "/orders?page=0&size=1&sort=id,desc",
                token, null), 200);
        assertThat(history.get("totalElements").asLong()).isEqualTo(2);
        assertThat(history.get("content").size()).isEqualTo(1);
        assertThat(history.get("content").get(0).get("id").asLong()).isEqualTo(secondOrderId);
        JsonNode items = json(request("GET", "/orders/" + firstOrderId
                + "/items?page=0&size=1&sort=book.id,desc", token, null), 200);
        assertThat(items.get("totalElements").asLong()).isEqualTo(2);
        assertThat(items.get("content").size()).isEqualTo(1);
        assertThat(items.get("content").get(0).get("bookId").asLong())
                .isEqualTo(secondBook.getId());
    }

    @Test
    void usersCanOnlyReadItemsFromTheirOwnOrders() throws Exception {
        addBook(firstBook, 1);
        JsonNode order = placeOrder();
        long orderId = order.get("id").asLong();
        long itemId = order.get("orderItems").get(0).get("id").asLong();
        String itemPath = "/orders/" + orderId + "/items/" + itemId;

        assertThat(json(request("GET", itemPath, token, null), 200)
                .get("bookId").asLong()).isEqualTo(firstBook.getId());
        String otherToken = registerUser();
        assertThat(request("GET", "/orders/" + orderId + "/items", otherToken, null)
                .statusCode()).isEqualTo(404);
        assertThat(request("GET", itemPath, otherToken, null).statusCode()).isEqualTo(404);
        assertThat(json(request("GET", "/orders", otherToken, null), 200)
                .get("totalElements").asLong()).isZero();

        addBook(secondBook, 1);
        long otherOrderId = placeOrder().get("id").asLong();
        assertThat(request("GET", "/orders/" + otherOrderId + "/items/" + itemId,
                token, null).statusCode()).isEqualTo(404);
        assertThat(request("GET", "/orders/9223372036854775807/items", token, null)
                .statusCode()).isEqualTo(404);
    }

    @Test
    void onlyAdminCanUpdateStatus() throws Exception {
        addBook(firstBook, 1);
        long orderId = placeOrder().get("id").asLong();
        String path = "/orders/" + orderId;
        String body = "{\"status\":\"DELIVERED\"}";

        assertThat(request("PATCH", path, token, body).statusCode()).isEqualTo(403);
        String adminToken = jwtUtil.generateToken("admin@bookstore.local");
        assertThat(json(request("PATCH", path, adminToken, body), 200)
                .get("status").asText()).isEqualTo("DELIVERED");
        assertThat(json(request("GET", "/orders", token, null), 200)
                .get("content").get(0).get("status").asText()).isEqualTo("DELIVERED");
        assertThat(request("PATCH", "/orders/9223372036854775807", adminToken, body)
                .statusCode()).isEqualTo(404);
    }

    @Test
    void invalidRequestsAndEmptyCartAreRejected() throws Exception {
        assertThat(request("POST", "/orders", token,
                "{\"shippingAddress\":\"Kyiv\"}").statusCode()).isEqualTo(400);
        addBook(firstBook, 1);
        assertThat(request("POST", "/orders", token,
                "{\"shippingAddress\":\"   \"}").statusCode()).isEqualTo(400);
        assertThat(request("POST", "/orders", token, "{}").statusCode()).isEqualTo(400);
        assertThat(request("POST", "/orders", token,
                "{\"shippingAddress\":\"" + "a".repeat(256) + "\"}")
                .statusCode()).isEqualTo(400);
        long orderId = placeOrder().get("id").asLong();
        String adminToken = jwtUtil.generateToken("admin@bookstore.local");
        assertThat(request("PATCH", "/orders/" + orderId, adminToken, "{}")
                .statusCode()).isEqualTo(400);
        assertThat(request("PATCH", "/orders/" + orderId, adminToken,
                "{\"status\":\"UNKNOWN\"}").statusCode()).isEqualTo(400);
    }

    @Test
    void orderEndpointsRequireAuthentication() throws Exception {
        assertThat(request("GET", "/orders", null, null).statusCode()).isIn(401, 403);
        assertThat(request("POST", "/orders", null,
                "{\"shippingAddress\":\"Kyiv\"}").statusCode()).isIn(401, 403);
        assertThat(request("GET", "/orders/1/items", null, null).statusCode()).isIn(401, 403);
        assertThat(request("GET", "/orders/1/items/1", null, null).statusCode()).isIn(401, 403);
        assertThat(request("PATCH", "/orders/1", null,
                "{\"status\":\"DELIVERED\"}").statusCode()).isIn(401, 403);
    }

    @Test
    void historySurvivesBookDeletionAndOrdersUseSoftDelete() throws Exception {
        addBook(firstBook, 1);
        JsonNode order = placeOrder();
        long orderId = order.get("id").asLong();
        long itemId = order.get("orderItems").get(0).get("id").asLong();
        bookRepository.deleteById(firstBook.getId());

        assertThat(json(request("GET", "/orders/" + orderId + "/items/" + itemId,
                token, null), 200).get("bookId").asLong()).isEqualTo(firstBook.getId());
        assertThat(json(request("GET", "/orders", token, null), 200)
                .get("totalElements").asLong()).isEqualTo(1);
        orderRepository.deleteById(orderId);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM orders WHERE id = ?", Boolean.class, orderId)).isTrue();
        assertThat(jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM order_items WHERE id = ?", Boolean.class, itemId)).isTrue();
        assertThat(json(request("GET", "/orders", token, null), 200)
                .get("totalElements").asLong()).isZero();
        assertThat(request("GET", "/orders/" + orderId + "/items", token, null)
                .statusCode()).isEqualTo(404);
    }

    @Test
    void cartItemPathsMatchTheRequiredApi() throws Exception {
        addBook(firstBook, 1);
        long itemId = json(request("GET", "/cart", token, null), 200)
                .get("cartItems").get(0).get("id").asLong();
        String path = "/cart/cart-items/" + itemId;
        assertThat(json(request("PUT", path, token, "{\"quantity\":3}"), 200)
                .get("cartItems").get(0).get("quantity").asInt()).isEqualTo(3);
        assertThat(request("DELETE", path, token, null).statusCode()).isEqualTo(204);
    }

    private String registerUser() throws Exception {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto();
        request.setEmail(UUID.randomUUID() + "@example.com");
        request.setPassword("password123");
        request.setRepeatPassword("password123");
        request.setFirstName("Test");
        request.setLastName("User");
        userService.register(request);
        return jwtUtil.generateToken(request.getEmail());
    }

    private Book saveBook(String price) {
        Book book = new Book();
        book.setTitle("Test book");
        book.setAuthor("Test author");
        book.setIsbn(UUID.randomUUID().toString());
        book.setPrice(new BigDecimal(price));
        return bookRepository.save(book);
    }

    private void addBook(Book book, int quantity) throws Exception {
        json(request("POST", "/cart", token,
                "{\"bookId\":" + book.getId() + ",\"quantity\":" + quantity + "}"), 200);
    }

    private JsonNode placeOrder() throws Exception {
        return json(request("POST", "/orders", token,
                "{\"shippingAddress\":\"Kyiv, Shevchenko ave, 1\"}"), 201);
    }

    private HttpResponse<String> request(String method, String path, String authToken,
            String body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api" + path));
        if (authToken != null) {
            builder.header("Authorization", "Bearer " + authToken);
        }
        if (body != null) {
            builder.header("Content-Type", "application/json");
        }
        builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body));
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private JsonNode json(HttpResponse<String> response, int status) {
        assertThat(response.statusCode()).as(response.body()).isEqualTo(status);
        return objectMapper.readTree(response.body());
    }
}
