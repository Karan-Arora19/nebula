package pse.nebula.merchandise.cart.application.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import pse.nebula.merchandise.cart.application.service.CartService;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Test
    void getCart_shouldReturnOk() throws Exception {
        var mockMvc = MockMvcBuilders.standaloneSetup(new CartController(cartService)).build();
        mockMvc.perform(get("/api/v1/cart/testuser"))
                .andExpect(status().isOk());
    }
}
