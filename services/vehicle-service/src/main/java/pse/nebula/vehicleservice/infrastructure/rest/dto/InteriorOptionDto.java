package pse.nebula.vehicleservice.infrastructure.rest.dto;

import pse.nebula.vehicleservice.domain.model.CarType;
import pse.nebula.vehicleservice.domain.model.Interior;
import pse.nebula.vehicleservice.domain.model.InteriorPrice;

import java.math.BigDecimal;

/**
 * DTO for interior option with resolved price.
 */
public record InteriorOptionDto(
        Integer id,
        String name,
        String description,
        String image,
        String visualKey,
        Integer cost
) {
    /**
     * Creates an InteriorOptionDto from an Interior entity.
     * Filters prices by the specified car type.
     */
    public static InteriorOptionDto fromEntity(Interior interior, CarType carType) {
        Integer resolvedCost = interior.getPrices().stream()
                .filter(p -> p.getCarType() == carType)
                .findFirst()
                .map(InteriorPrice::getPrice)
                .map(BigDecimal::intValue)
                .orElse(0);

        return new InteriorOptionDto(
                interior.getId(),
                interior.getName(),
                interior.getDescription(),
                interior.getImage(),
                interior.getVisualKey(),
                resolvedCost
        );
    }
}

