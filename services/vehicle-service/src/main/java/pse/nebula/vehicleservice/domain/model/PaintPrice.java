package pse.nebula.vehicleservice.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents the price of a paint option for a specific car type.
 * Uses a composite primary key (paint_id, car_type).
 */
@Getter
@Entity
@Table(name = "paint_price", schema = "vehicle_service")
@IdClass(PaintPrice.PaintPriceId.class)
public class PaintPrice {

    // Getters and setters
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paint_id", nullable = false)
    private Paint paint;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "car_type", nullable = false)
    private CarType carType;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    // Default constructor for JPA
    protected PaintPrice() {
    }

    public PaintPrice(CarType carType, BigDecimal price) {
        this.carType = carType;
        this.price = price;
    }

    void setPaint(Paint paint) {
        this.paint = paint;
    }

    /**
     * Composite primary key for PaintPrice.
     */
    public static class PaintPriceId implements Serializable {
        private Integer paint;
        private CarType carType;

        public PaintPriceId() {
        }

        public PaintPriceId(Integer paint, CarType carType) {
            this.paint = paint;
            this.carType = carType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PaintPriceId that = (PaintPriceId) o;
            return Objects.equals(paint, that.paint) && carType == that.carType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(paint, carType);
        }
    }
}

