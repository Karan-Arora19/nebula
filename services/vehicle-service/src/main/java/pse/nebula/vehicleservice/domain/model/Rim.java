package pse.nebula.vehicleservice.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a rim/wheel option.
 */
@Getter
@Entity
@Table(name = "rim", schema = "vehicle_service")
public class Rim {

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

    @OneToMany(mappedBy = "rim", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RimPrice> prices = new ArrayList<>();

    // Default constructor for JPA
    protected Rim() {
    }

    public Rim(String name, String description, String image, String visualKey) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.visualKey = visualKey;
    }

    // Helper method to add price
    public void addPrice(RimPrice price) {
        prices.add(price);
        price.setRim(this);
    }
}

