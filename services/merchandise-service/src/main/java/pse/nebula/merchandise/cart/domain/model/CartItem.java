package pse.nebula.merchandise.cart.domain.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Integer quantity;
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_user_id")
    private Cart cart;

    public CartItem() {}

    public CartItem(Long productId, Integer quantity, BigDecimal price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public static CartItemBuilder builder() { return new CartItemBuilder(); }

    public static class CartItemBuilder {
        private Long productId;
        private Integer quantity;
        private BigDecimal price;

        public CartItemBuilder productId(Long productId) { this.productId = productId; return this; }
        public CartItemBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public CartItemBuilder price(BigDecimal price) { this.price = price; return this; }
        public CartItem build() {
            return new CartItem(productId, quantity, price);
        }
    }
}
