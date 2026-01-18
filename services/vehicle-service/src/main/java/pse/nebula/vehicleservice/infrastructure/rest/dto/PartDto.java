package pse.nebula.vehicleservice.infrastructure.rest.dto;

/**
 * DTO for a configuration part/option within a category.
 * Used in the category-based configuration response structure.
 */
public record PartDto(
        String id,
        String name,
        Integer cost,
        String visualKey,
        String description,
        String hex,
        String image
) {
    /**
     * Creates a PartDto from a PaintOptionDto.
     */
    public static PartDto fromPaint(PaintOptionDto paint) {
        return new PartDto(
                "paint-" + paint.id(),
                paint.name(),
                paint.cost(),
                paint.visualKey(),
                paint.description(),
                paint.hex(),
                null
        );
    }

    /**
     * Creates a PartDto from a RimOptionDto.
     */
    public static PartDto fromRim(RimOptionDto rim) {
        return new PartDto(
                "rim-" + rim.id(),
                rim.name(),
                rim.cost(),
                rim.visualKey(),
                rim.description(),
                null,
                rim.image()
        );
    }

    /**
     * Creates a PartDto from an InteriorOptionDto.
     */
    public static PartDto fromInterior(InteriorOptionDto interior) {
        return new PartDto(
                "interior-" + interior.id(),
                interior.name(),
                interior.cost(),
                interior.visualKey(),
                interior.description(),
                null,
                interior.image()
        );
    }
}

