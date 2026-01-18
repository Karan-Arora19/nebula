package pse.nebula.vehicleservice.infrastructure.rest.dto;

import java.util.List;

/**
 * DTO for a configuration category (paint, rims, interior).
 * Used in the category-based configuration response structure.
 */
public record CategoryDto(
        String id,
        String name,
        String icon,
        List<PartDto> parts
) {
    /**
     * Creates a paint category from paint options.
     */
    public static CategoryDto createPaintCategory(List<PaintOptionDto> paints) {
        List<PartDto> parts = paints.stream()
                .map(PartDto::fromPaint)
                .toList();

        return new CategoryDto(
                "paint",
                "Exterior Color",
                "🎨",
                parts
        );
    }

    /**
     * Creates a rims category from rim options.
     */
    public static CategoryDto createRimsCategory(List<RimOptionDto> rims) {
        List<PartDto> parts = rims.stream()
                .map(PartDto::fromRim)
                .toList();

        return new CategoryDto(
                "rims",
                "Rims",
                "⭕",
                parts
        );
    }

    /**
     * Creates an interior category from interior options.
     */
    public static CategoryDto createInteriorCategory(List<InteriorOptionDto> interiors) {
        List<PartDto> parts = interiors.stream()
                .map(PartDto::fromInterior)
                .toList();

        return new CategoryDto(
                "interior",
                "Interior",
                "🪑",
                parts
        );
    }
}

