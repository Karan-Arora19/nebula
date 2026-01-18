package pse.nebula.merchandise.cart.application.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import pse.nebula.merchandise.application.dto.ProductResponse;
import pse.nebula.merchandise.application.service.ProductService;
import pse.nebula.merchandise.cart.application.dto.AddCartItemRequest;
import pse.nebula.merchandise.cart.application.dto.CartResponse;
import pse.nebula.merchandise.cart.domain.model.Cart;
import pse.nebula.merchandise.cart.domain.model.CartItem;
import pse.nebula.merchandise.cart.domain.repository.CartRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    @Test
    void addItem_createsCartAndAddsNewItem() {
        AddCartItemRequest request = new AddCartItemRequest(1L, 2);
        when(productService.findById(1L)).thenReturn(productResponse());
        when(cartRepository.findById("user-1")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.addItem("user-1", request);

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(cartCaptor.capture());
        Cart saved = cartCaptor.getValue();

        assertEquals("user-1", response.getUserId());
        assertEquals(1, response.getItems().size());
        assertEquals(2, response.getItems().get(0).getQuantity());
        assertEquals(1, saved.getItems().size());
        assertEquals(BigDecimal.TEN, saved.getItems().get(0).getPrice());
    }

    @Test
    void addItem_incrementsQuantityWhenItemExists() {
        Cart existingCart = new Cart("user-1", new ArrayList<>());
        CartItem item = CartItem.builder().productId(1L).quantity(1).price(BigDecimal.ONE).build();
        item.setCart(existingCart);
        existingCart.setItems(new ArrayList<>(List.of(item)));

        when(productService.findById(1L)).thenReturn(productResponse());
        when(cartRepository.findById("user-1")).thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.addItem("user-1", new AddCartItemRequest(1L, 2));

        assertEquals(1, response.getItems().size());
        assertEquals(3, response.getItems().get(0).getQuantity());
        assertEquals(BigDecimal.TEN, response.getItems().get(0).getPrice());
    }

    @Test
    void removeItem_removesOnlyMatchingProduct() {
        Cart cart = new Cart("user-1", new ArrayList<>());
        CartItem keep = CartItem.builder().productId(1L).quantity(1).price(BigDecimal.ONE).build();
        keep.setCart(cart);
        CartItem remove = CartItem.builder().productId(2L).quantity(1).price(BigDecimal.ONE).build();
        remove.setCart(cart);
        cart.setItems(new ArrayList<>(List.of(keep, remove)));

        when(cartRepository.findById("user-1")).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.removeItem("user-1", 2L);

        assertEquals(1, response.getItems().size());
        assertEquals(1L, response.getItems().get(0).getProductId());
    }

    @Test
    void getCart_returnsEmptyCartWhenMissing() {
        when(cartRepository.findById("missing")).thenReturn(Optional.empty());

        CartResponse response = cartService.getCart("missing");

        assertEquals("missing", response.getUserId());
        assertNotNull(response.getItems());
        assertEquals(0, response.getItems().size());
    }

    private ProductResponse productResponse() {
        return new ProductResponse(1L, "name", "desc", BigDecimal.TEN, 5, "img");
    }
}
