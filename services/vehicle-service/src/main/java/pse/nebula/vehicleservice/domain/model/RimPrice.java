package pse.nebula.vehicleservice.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents the price of a rim option for a specific car type.
 * Uses a composite primary key (rim_id, car_type).
 */
@Getter
@Entity
@Table(name = "rim_price", schema = "vehicle_service")
@IdClass(RimPrice.RimPriceId.class)
public class RimPrice {

    // Getters and setters
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rim_id", nullable = false)
    private Rim rim;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "car_type", nullable = false)
    private CarType carType;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    // Default constructor for JPA
    protected RimPrice() {
    }

    public RimPrice(CarType carType, BigDecimal price) {
        this.carType = carType;
        this.price = price;
    }

    void setRim(Rim rim) {
        this.rim = rim;
    }

    /**
     * Composite primary key for RimPrice.
     */
    public static class RimPriceId implements Serializable {
        private Integer rim;
        private CarType carType;

        public RimPriceId() {
        }

        public RimPriceId(Integer rim, CarType carType) {
            this.rim = rim;
            this.carType = carType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RimPriceId that = (RimPriceId) o;
            return Objects.equals(rim, that.rim) && carType == that.carType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(rim, carType);
        }
    }
}

