package pse.nebula.merchandise.application.dto;

import java.math.BigDecimal;

public class ProductResponse {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stock;
        private String imageUrl;
        private String category;
        private String badge;
        private BigDecimal rating;
        private Integer reviews;

        public ProductResponse() {}

        public ProductResponse(Long id, String name, String description, BigDecimal price, Integer stock, String imageUrl, String category, String badge, BigDecimal rating, Integer reviews) {
                this.id = id;
                this.name = name;
                this.description = description;
                this.price = price;
                this.stock = stock;
                this.imageUrl = imageUrl;
                this.category = category;
                this.badge = badge;
                this.rating = rating;
                this.reviews = reviews;
        }

        public ProductResponse(Long id, String name, String description, BigDecimal price, Integer stock, String imageUrl) {
                this.id = id;
                this.name = name;
                this.description = description;
                this.price = price;
                this.stock = stock;
                this.imageUrl = imageUrl;
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getDescription() {
                return description;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public BigDecimal getPrice() {
                return price;
        }

        public void setPrice(BigDecimal price) {
                this.price = price;
        }

        public Integer getStock() {
                return stock;
        }

        public void setStock(Integer stock) {
                this.stock = stock;
        }

        public String getImageUrl() {
                return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
        }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getBadge() { return badge; }
        public void setBadge(String badge) { this.badge = badge; }
        public BigDecimal getRating() { return rating; }
        public void setRating(BigDecimal rating) { this.rating = rating; }
        public Integer getReviews() { return reviews; }
        public void setReviews(Integer reviews) { this.reviews = reviews; }
}
