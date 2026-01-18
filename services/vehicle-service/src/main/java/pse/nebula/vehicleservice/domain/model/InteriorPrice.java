package pse.nebula.vehicleservice.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents the price of an interior option for a specific car type.
 * Uses a composite primary key (interior_id, car_type).
 */
@Getter
@Entity
@Table(name = "interior_price", schema = "vehicle_service")
@IdClass(InteriorPrice.InteriorPriceId.class)
public class InteriorPrice {

    // Getters and setters
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interior_id", nullable = false)
    private Interior interior;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "car_type", nullable = false)
    private CarType carType;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    // Default constructor for JPA
    protected InteriorPrice() {
    }

    public InteriorPrice(CarType carType, BigDecimal price) {
        this.carType = carType;
        this.price = price;
    }

    void setInterior(Interior interior) {
        this.interior = interior;
    }

    /**
     * Composite primary key for InteriorPrice.
     */
    public static class InteriorPriceId implements Serializable {
        private Integer interior;
        private CarType carType;

        public InteriorPriceId() {
        }

        public InteriorPriceId(Integer interior, CarType carType) {
            this.interior = interior;
            this.carType = carType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InteriorPriceId that = (InteriorPriceId) o;
            return Objects.equals(interior, that.interior) && carType == that.carType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(interior, carType);
        }
    }
}

