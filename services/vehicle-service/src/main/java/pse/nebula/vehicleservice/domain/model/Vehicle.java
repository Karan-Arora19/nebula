package pse.nebula.vehicleservice.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Represents a vehicle in the inventory.
 */
@Entity
@Table(name = "vehicle", schema = "vehicle_service")
@Getter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "car_name", nullable = false)
    private String carName;

    @Enumerated(EnumType.STRING)
    @Column(name = "car_type", nullable = false)
    private CarType carType;

    @Column(name = "horse_power", nullable = false)
    private Integer horsePower;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "model_path", nullable = false)
    private String modelPath;

    // Default constructor for JPA
    protected Vehicle() {
    }

    public Vehicle(String carName, CarType carType, Integer horsePower, BigDecimal basePrice, String image, String modelPath) {
        this.carName = carName;
        this.carType = carType;
        this.horsePower = horsePower;
        this.basePrice = basePrice;
        this.image = image;
        this.modelPath = modelPath;
    }
}

