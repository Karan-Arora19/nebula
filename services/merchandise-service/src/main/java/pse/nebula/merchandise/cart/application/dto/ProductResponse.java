package pse.nebula.merchandise.cart.application.dto;

import java.math.BigDecimal;

public class ProductResponse extends pse.nebula.merchandise.application.dto.ProductResponse {

    public ProductResponse() {
        super();
    }

    public ProductResponse(Long id, String name, String description, BigDecimal price, Integer stock, String imageUrl) {
        super(id, name, description, price, stock, imageUrl);
    }
}
