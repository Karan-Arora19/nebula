package pse.nebula.vehicleservice.infrastructure.rest.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pse.nebula.vehicleservice.domain.model.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTO Mapping Tests")
class DtoMappingTest {

    @Test
    @DisplayName("VehicleDto should map from Vehicle entity")
    void vehicleDtoShouldMapFromEntity() {
        // Given
        Vehicle vehicle = new Vehicle(
                "Furari",
                CarType.SPORTS,
                670,
                new BigDecimal("245000.00"),
                "furarri-hero",
                "/models/furarri.glb"
        );

        // When
        VehicleDto dto = VehicleDto.fromEntity(vehicle);

        // Then
        assertThat(dto.carName()).isEqualTo("Furari");
        assertThat(dto.carType()).isEqualTo(CarType.SPORTS);
        assertThat(dto.horsePower()).isEqualTo(670);
        assertThat(dto.basePrice()).isEqualByComparingTo(new BigDecimal("245000.00"));
        assertThat(dto.image()).isEqualTo("furarri-hero");
        assertThat(dto.modelPath()).isEqualTo("/models/furarri.glb");
    }

    @Test
    @DisplayName("PaintOptionDto should map from Paint entity with resolved price")
    void paintOptionDtoShouldMapWithPrice() {
        // Given
        Paint paint = new Paint("Black", "Timeless black finish", "black", "#000000");
        paint.addPrice(new PaintPrice(CarType.SPORTS, new BigDecimal("500.00")));

        // When
        PaintOptionDto dto = PaintOptionDto.fromEntity(paint, CarType.SPORTS);

        // Then
        assertThat(dto.name()).isEqualTo("Black");
        assertThat(dto.description()).isEqualTo("Timeless black finish");
        assertThat(dto.visualKey()).isEqualTo("black");
        assertThat(dto.hex()).isEqualTo("#000000");
        assertThat(dto.cost()).isEqualTo(500);
    }

    @Test
    @DisplayName("PaintOptionDto should return zero cost when no prices available for car type")
    void paintOptionDtoShouldReturnZeroWhenNoPrices() {
        // Given
        Paint paint = new Paint("Empty", "No prices", "empty", "#FFFFFF");

        // When
        PaintOptionDto dto = PaintOptionDto.fromEntity(paint, CarType.SPORTS);

        // Then
        assertThat(dto.cost()).isEqualTo(0);
    }

    @Test
    @DisplayName("RimOptionDto should map from Rim entity with resolved price")
    void rimOptionDtoShouldMapWithPrice() {
        // Given
        Rim rim = new Rim("Sport Alloy", "Sporty wheels", "rim-sport", "sport");
        rim.addPrice(new RimPrice(CarType.SPORTS, new BigDecimal("2500.00")));

        // When
        RimOptionDto dto = RimOptionDto.fromEntity(rim, CarType.SPORTS);

        // Then
        assertThat(dto.name()).isEqualTo("Sport Alloy");
        assertThat(dto.description()).isEqualTo("Sporty wheels");
        assertThat(dto.image()).isEqualTo("rim-sport");
        assertThat(dto.visualKey()).isEqualTo("sport");
        assertThat(dto.cost()).isEqualTo(2500);
    }

    @Test
    @DisplayName("InteriorOptionDto should map from Interior entity with resolved price")
    void interiorOptionDtoShouldMapWithPrice() {
        // Given
        Interior interior = new Interior("Black Leather", "Premium leather", "interior-black", "black");
        interior.addPrice(new InteriorPrice(CarType.SPORTS, new BigDecimal("3200.00")));

        // When
        InteriorOptionDto dto = InteriorOptionDto.fromEntity(interior, CarType.SPORTS);

        // Then
        assertThat(dto.name()).isEqualTo("Black Leather");
        assertThat(dto.description()).isEqualTo("Premium leather");
        assertThat(dto.image()).isEqualTo("interior-black");
        assertThat(dto.visualKey()).isEqualTo("black");
        assertThat(dto.cost()).isEqualTo(3200);
    }

    @Test
    @DisplayName("ErrorResponse should create with all fields")
    void errorResponseShouldCreateWithAllFields() {
        // When
        ErrorResponse error = ErrorResponse.of(404, "Not Found", "Vehicle not found", "/api/v1/vehicles/999");

        // Then
        assertThat(error.status()).isEqualTo(404);
        assertThat(error.error()).isEqualTo("Not Found");
        assertThat(error.message()).isEqualTo("Vehicle not found");
        assertThat(error.path()).isEqualTo("/api/v1/vehicles/999");
        assertThat(error.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("PartDto should map from PaintOptionDto")
    void partDtoShouldMapFromPaint() {
        // Given
        PaintOptionDto paint = new PaintOptionDto(1, "Black", "Deep black", "black", "#000000", 500);

        // When
        PartDto part = PartDto.fromPaint(paint);

        // Then
        assertThat(part.id()).isEqualTo("paint-1");
        assertThat(part.name()).isEqualTo("Black");
        assertThat(part.description()).isEqualTo("Deep black");
        assertThat(part.visualKey()).isEqualTo("black");
        assertThat(part.hex()).isEqualTo("#000000");
        assertThat(part.cost()).isEqualTo(500);
        assertThat(part.image()).isNull();
    }

    @Test
    @DisplayName("PartDto should map from RimOptionDto")
    void partDtoShouldMapFromRim() {
        // Given
        RimOptionDto rim = new RimOptionDto(2, "Sport 19\"", "Sport wheels", "rim-sport", "sport", 0);

        // When
        PartDto part = PartDto.fromRim(rim);

        // Then
        assertThat(part.id()).isEqualTo("rim-2");
        assertThat(part.name()).isEqualTo("Sport 19\"");
        assertThat(part.description()).isEqualTo("Sport wheels");
        assertThat(part.visualKey()).isEqualTo("sport");
        assertThat(part.image()).isEqualTo("rim-sport");
        assertThat(part.cost()).isEqualTo(0);
        assertThat(part.hex()).isNull();
    }

    @Test
    @DisplayName("PartDto should map from InteriorOptionDto")
    void partDtoShouldMapFromInterior() {
        // Given
        InteriorOptionDto interior = new InteriorOptionDto(3, "Black Leather", "Premium leather", "interior-black", "black", 0);

        // When
        PartDto part = PartDto.fromInterior(interior);

        // Then
        assertThat(part.id()).isEqualTo("interior-3");
        assertThat(part.name()).isEqualTo("Black Leather");
        assertThat(part.description()).isEqualTo("Premium leather");
        assertThat(part.visualKey()).isEqualTo("black");
        assertThat(part.image()).isEqualTo("interior-black");
        assertThat(part.cost()).isEqualTo(0);
        assertThat(part.hex()).isNull();
    }

    @Test
    @DisplayName("CategoryDto should create paint category")
    void categoryDtoShouldCreatePaintCategory() {
        // Given
        PaintOptionDto paint = new PaintOptionDto(1, "Black", "Deep black", "black", "#000000", 500);

        // When
        CategoryDto category = CategoryDto.createPaintCategory(java.util.List.of(paint));

        // Then
        assertThat(category.id()).isEqualTo("paint");
        assertThat(category.name()).isEqualTo("Exterior Color");
        assertThat(category.icon()).isEqualTo("🎨");
        assertThat(category.parts()).hasSize(1);
        assertThat(category.parts().get(0).name()).isEqualTo("Black");
    }

    @Test
    @DisplayName("CategoryDto should create rims category")
    void categoryDtoShouldCreateRimsCategory() {
        // Given
        RimOptionDto rim = new RimOptionDto(1, "Sport 19\"", "Sport wheels", "rim-sport", "sport", 0);

        // When
        CategoryDto category = CategoryDto.createRimsCategory(java.util.List.of(rim));

        // Then
        assertThat(category.id()).isEqualTo("rims");
        assertThat(category.name()).isEqualTo("Rims");
        assertThat(category.icon()).isEqualTo("⭕");
        assertThat(category.parts()).hasSize(1);
    }

    @Test
    @DisplayName("CategoryDto should create interior category")
    void categoryDtoShouldCreateInteriorCategory() {
        // Given
        InteriorOptionDto interior = new InteriorOptionDto(1, "Black Leather", "Premium", "interior-black", "black", 0);

        // When
        CategoryDto category = CategoryDto.createInteriorCategory(java.util.List.of(interior));

        // Then
        assertThat(category.id()).isEqualTo("interior");
        assertThat(category.name()).isEqualTo("Interior");
        assertThat(category.icon()).isEqualTo("🪑");
        assertThat(category.parts()).hasSize(1);
    }
}

