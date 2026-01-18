package pse.nebula.merchandise.domain.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
// Removed Lombok, added explicit methods

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1024)
    private String description;

    @Column(length = 255)
    private String category;

    @Column(length = 100)
    private String badge;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(precision = 3, scale = 1)
    private BigDecimal rating;

    @Column
    private Integer reviews;

    @Column(name = "image_url")
    private String imageUrl;

    public Product() {}

    public Product(Long id, String name, String description, String category, String badge, BigDecimal price, Integer stock, BigDecimal rating, Integer reviews, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.badge = badge;
        this.price = price;
        this.stock = stock;
        this.rating = rating;
        this.reviews = reviews;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public Integer getReviews() { return reviews; }
    public void setReviews(Integer reviews) { this.reviews = reviews; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public static ProductBuilder builder() { return new ProductBuilder(); }

    public static class ProductBuilder {
        private Long id;
        private String name;
        private String description;
        private String category;
        private String badge;
        private BigDecimal price;
        private Integer stock;
        private BigDecimal rating;
        private Integer reviews;
        private String imageUrl;

        public ProductBuilder id(Long id) { this.id = id; return this; }
        public ProductBuilder name(String name) { this.name = name; return this; }
        public ProductBuilder description(String description) { this.description = description; return this; }
        public ProductBuilder category(String category) { this.category = category; return this; }
        public ProductBuilder badge(String badge) { this.badge = badge; return this; }
        public ProductBuilder price(BigDecimal price) { this.price = price; return this; }
        public ProductBuilder stock(Integer stock) { this.stock = stock; return this; }
        public ProductBuilder rating(BigDecimal rating) { this.rating = rating; return this; }
        public ProductBuilder reviews(Integer reviews) { this.reviews = reviews; return this; }
        public ProductBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public Product build() {
            return new Product(id, name, description, category, badge, price, stock, rating, reviews, imageUrl);
        }
    }
}
