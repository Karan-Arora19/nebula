package pse.nebula.merchandise.cart.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pse.nebula.merchandise.cart.application.dto.AddCartItemRequest;
import pse.nebula.merchandise.cart.application.dto.CartResponse;
import pse.nebula.merchandise.cart.application.service.CartService;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{userId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse addItem(@PathVariable String userId, @Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(userId, request);
    }

    @GetMapping("/{userId}")
    public CartResponse getCart(@PathVariable String userId) {
        return cartService.getCart(userId);
    }

    @DeleteMapping("/{userId}/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable String userId, @PathVariable Long productId) {
        cartService.removeItem(userId, productId);
    }
}
