package pse.nebula.vehicleservice.infrastructure.rest.dto;

import pse.nebula.vehicleservice.domain.model.CarType;
import pse.nebula.vehicleservice.domain.model.Paint;
import pse.nebula.vehicleservice.domain.model.PaintPrice;

import java.math.BigDecimal;

/**
 * DTO for paint option with resolved price.
 */
public record PaintOptionDto(
        Integer id,
        String name,
        String description,
        String visualKey,
        String hex,
        Integer cost
) {
    /**
     * Creates a PaintOptionDto from a Paint entity.
     * Filters prices by the specified car type.
     */
    public static PaintOptionDto fromEntity(Paint paint, CarType carType) {
        Integer resolvedCost = paint.getPrices().stream()
                .filter(p -> p.getCarType() == carType)
                .findFirst()
                .map(PaintPrice::getPrice)
                .map(BigDecimal::intValue)
                .orElse(0);

        return new PaintOptionDto(
                paint.getId(),
                paint.getName(),
                paint.getDescription(),
                paint.getVisualKey(),
                paint.getHex(),
                resolvedCost
        );
    }
}

