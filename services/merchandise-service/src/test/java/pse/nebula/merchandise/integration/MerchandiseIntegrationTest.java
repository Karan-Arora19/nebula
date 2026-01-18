package pse.nebula.merchandise.integration;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import pse.nebula.merchandise.application.dto.ProductRequest;
import pse.nebula.merchandise.cart.application.dto.AddCartItemRequest;
import pse.nebula.merchandise.cart.domain.repository.CartRepository;
import pse.nebula.merchandise.domain.model.Product;
import pse.nebula.merchandise.domain.repository.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MerchandiseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Schema creation is handled by test profile (spring.jpa.hibernate.ddl-auto=create-drop)

    @BeforeEach
    void setup() {
        // Clear repositories after schema creation
        cartRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void productEndpoints_createThenList() throws Exception {
        ProductRequest request = new ProductRequest("Hat", "Blue hat", new BigDecimal("9.99"), 10, "img");

        mockMvc.perform(post("/api/v1/merchandise/products")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Hat"));

        mockMvc.perform(get("/api/v1/merchandise/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Hat"))
                .andExpect(jsonPath("$[0].price").value(9.99));
    }

    @Test
    void cartEndpoints_addAndRemoveItems() throws Exception {
        Product product = productRepository.save(Product.builder()
                .name("Shirt")
                .description("Blue shirt")
                .price(new BigDecimal("15.00"))
                .stock(5)
                .imageUrl("img")
                .build());

        AddCartItemRequest addRequest = new AddCartItemRequest(product.getId(), 2);

        mockMvc.perform(post("/api/v1/cart/integration-user/items")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items[0].productId").value(product.getId().intValue()))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        mockMvc.perform(get("/api/v1/cart/integration-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productId").value(product.getId().intValue()))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        mockMvc.perform(delete("/api/v1/cart/integration-user/items/" + product.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/cart/integration-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }
}
