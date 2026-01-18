package pse.nebula.merchandise.cart.domain.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "carts")
public class Cart {
    @Id
    private String userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> items = new ArrayList<>();

    public Cart() {}

    public Cart(String userId, List<CartItem> items) {
        this.userId = userId;
        this.items = items;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public static CartBuilder builder() { return new CartBuilder(); }

    public static class CartBuilder {
        private String userId;
        private List<CartItem> items = new ArrayList<>();

        public CartBuilder userId(String userId) { this.userId = userId; return this; }
        public CartBuilder items(List<CartItem> items) { this.items = items; return this; }
        public Cart build() {
            return new Cart(userId, items);
        }
    }
}
