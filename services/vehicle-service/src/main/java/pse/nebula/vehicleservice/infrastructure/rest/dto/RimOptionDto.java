package pse.nebula.vehicleservice.infrastructure.rest.dto;

import pse.nebula.vehicleservice.domain.model.CarType;
import pse.nebula.vehicleservice.domain.model.Rim;
import pse.nebula.vehicleservice.domain.model.RimPrice;

import java.math.BigDecimal;

/**
 * DTO for rim option with resolved price.
 */
public record RimOptionDto(
        Integer id,
        String name,
        String description,
        String image,
        String visualKey,
        Integer cost
) {
    /**
     * Creates a RimOptionDto from a Rim entity.
     * Filters prices by the specified car type.
     */
    public static RimOptionDto fromEntity(Rim rim, CarType carType) {
        Integer resolvedCost = rim.getPrices().stream()
                .filter(p -> p.getCarType() == carType)
                .findFirst()
                .map(RimPrice::getPrice)
                .map(BigDecimal::intValue)
                .orElse(0);

        return new RimOptionDto(
                rim.getId(),
                rim.getName(),
                rim.getDescription(),
                rim.getImage(),
                rim.getVisualKey(),
                resolvedCost
        );
    }
}

