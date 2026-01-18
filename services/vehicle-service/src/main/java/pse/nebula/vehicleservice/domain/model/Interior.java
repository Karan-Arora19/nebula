package pse.nebula.vehicleservice.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an interior color option.
 */
@Getter
@Entity
@Table(name = "interior", schema = "vehicle_service")
public class Interior {

    // Getters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "visual_key", nullable = false)
    private String visualKey;

    @OneToMany(mappedBy = "interior", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InteriorPrice> prices = new ArrayList<>();

    // Default constructor for JPA
    protected Interior() {
    }

    public Interior(String name, String description, String image, String visualKey) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.visualKey = visualKey;
    }

    // Helper method to add price
    public void addPrice(InteriorPrice price) {
        prices.add(price);
        price.setInterior(this);
    }
}

