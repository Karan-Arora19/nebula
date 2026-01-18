package pse.nebula.merchandise.cart.application.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pse.nebula.merchandise.application.dto.ProductResponse;
import pse.nebula.merchandise.application.service.ProductService;
import pse.nebula.merchandise.cart.application.dto.AddCartItemRequest;
import pse.nebula.merchandise.cart.application.dto.CartItemResponse;
import pse.nebula.merchandise.cart.application.dto.CartResponse;
import pse.nebula.merchandise.cart.domain.model.Cart;
import pse.nebula.merchandise.cart.domain.model.CartItem;
import pse.nebula.merchandise.cart.domain.repository.CartRepository;

@Service
@Transactional(readOnly = true)
public class CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    @Transactional
    public CartResponse addItem(String userId, AddCartItemRequest request) {
        ProductResponse product = productService.findById(request.getProductId());
        Cart cart = cartRepository.findById(userId).orElseGet(() -> new Cart(userId, new ArrayList<>()));
        List<CartItem> items = cart.getItems();
        CartItem existing = items.stream()
            .filter(item -> item.getProductId().equals(request.getProductId()))
            .findFirst()
            .orElse(null);
        BigDecimal price = product.getPrice();
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            existing.setPrice(price);
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .price(price)
                    .build();
            newItem.setCart(cart);
            items.add(newItem);
        }
        cart.setItems(items);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    public CartResponse getCart(String userId) {
        Cart cart = cartRepository.findById(userId).orElseGet(() -> new Cart(userId, new ArrayList<>()));
        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(String userId, Long productId) {
        Cart cart = cartRepository.findById(userId).orElseGet(() -> new Cart(userId, new ArrayList<>()));
        List<CartItem> items = cart.getItems();

        items.removeIf(item -> item.getProductId().equals(productId));
        for (CartItem item : items) {
            item.setCart(cart);
        }

        cartRepository.save(cart);
        return toResponse(cart);
    }



    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            itemResponses.add(new CartItemResponse(item.getProductId(), item.getQuantity(), item.getPrice()));
        }
        return new CartResponse(cart.getUserId(), itemResponses);
    }
}
