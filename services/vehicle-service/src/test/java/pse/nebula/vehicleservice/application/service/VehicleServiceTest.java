package pse.nebula.vehicleservice.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pse.nebula.vehicleservice.domain.exception.VehicleNotFoundException;
import pse.nebula.vehicleservice.domain.model.CarType;
import pse.nebula.vehicleservice.domain.model.Vehicle;
import pse.nebula.vehicleservice.domain.port.VehicleRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Tests")
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle sportsVehicle;
    private Vehicle sedanVehicle;

    @BeforeEach
    void setUp() {
        sportsVehicle = new Vehicle("Furari", CarType.SPORTS, 670, new BigDecimal("245000.00"), "furarri-hero", "/models/furarri.glb");
        sedanVehicle = new Vehicle("Dacia", CarType.SEDAN, 150, new BigDecimal("22000.00"), "dacia-hero", "/models/Dacia.glb");
    }

    @Nested
    @DisplayName("getAllVehicles")
    class GetAllVehiclesTests {

        @Test
        @DisplayName("should return all vehicles when vehicles exist")
        void shouldReturnAllVehicles() {
            // Given
            List<Vehicle> vehicles = List.of(sportsVehicle, sedanVehicle);
            when(vehicleRepository.findAll()).thenReturn(vehicles);

            // When
            List<Vehicle> result = vehicleService.getAllVehicles();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(sportsVehicle, sedanVehicle);
        }

        @Test
        @DisplayName("should return empty list when no vehicles exist")
        void shouldReturnEmptyListWhenNoVehicles() {
            // Given
            when(vehicleRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<Vehicle> result = vehicleService.getAllVehicles();

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getVehicleById")
    class GetVehicleByIdTests {

        @Test
        @DisplayName("should return vehicle when found")
        void shouldReturnVehicleWhenFound() {
            // Given
            when(vehicleRepository.findById(1)).thenReturn(Optional.of(sportsVehicle));

            // When
            Vehicle result = vehicleService.getVehicleById(1);

            // Then
            assertThat(result).isEqualTo(sportsVehicle);
            assertThat(result.getCarName()).isEqualTo("Furari");
            assertThat(result.getCarType()).isEqualTo(CarType.SPORTS);
        }

        @Test
        @DisplayName("should throw VehicleNotFoundException when vehicle not found")
        void shouldThrowExceptionWhenVehicleNotFound() {
            // Given
            when(vehicleRepository.findById(999)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> vehicleService.getVehicleById(999))
                    .isInstanceOf(VehicleNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }
}

