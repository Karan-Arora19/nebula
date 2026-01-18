package pse.nebula.merchandise.application.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import pse.nebula.merchandise.application.service.ProductService;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Test
    void getAllProducts_shouldReturnOk() throws Exception {
        var mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService)).build();
        mockMvc.perform(get("/api/v1/merchandise/products"))
                .andExpect(status().isOk());
    }
}
