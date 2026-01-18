package pse.nebula.merchandise.cart.application.dto;

import java.util.List;

public class CartResponse {
    private String userId;
    private List<CartItemResponse> items;

    public CartResponse() {}

    public CartResponse(String userId, List<CartItemResponse> items) {
        this.userId = userId;
        this.items = items;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }
}
