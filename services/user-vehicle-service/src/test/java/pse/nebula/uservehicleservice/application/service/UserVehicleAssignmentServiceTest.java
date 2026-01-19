package pse.nebula.uservehicleservice.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pse.nebula.uservehicleservice.domain.model.UserVehicle;
import pse.nebula.uservehicleservice.domain.repository.UserVehicleRepository;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.VehicleServiceClient;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.dto.VehicleDto;
import pse.nebula.uservehicleservice.infrastructure.exception.VehicleServiceException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserVehicleAssignmentService Unit Tests")
class UserVehicleAssignmentServiceTest {

    @Mock
    private UserVehicleRepository userVehicleRepository;

    @Mock
    private VehicleServiceClient vehicleServiceClient;

    private UserVehicleAssignmentService assignmentService;

    @BeforeEach
    void setUp() {
        assignmentService = new UserVehicleAssignmentService(userVehicleRepository, vehicleServiceClient);
    }

    @Nested
    @DisplayName("getOrAssignVehicle")
    class GetOrAssignVehicleTests {

        private static final String USER_ID = "user-123";

        @Test
        @DisplayName("should return existing assignment when user already has a vehicle")
        void shouldReturnExistingAssignment() {
            // Given
            UserVehicle existingVehicle = new UserVehicle(
                    USER_ID,
                    1,
                    "Furari",
                    LocalDate.now().plusMonths(6)
            );
            when(userVehicleRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existingVehicle));

            // When
            UserVehicle result = assignmentService.getOrAssignVehicle(USER_ID);

            // Then
            assertThat(result).isEqualTo(existingVehicle);
            assertThat(result.getVehicleName()).isEqualTo("Furari");
            verify(userVehicleRepository).findByUserId(USER_ID);
            verify(vehicleServiceClient, never()).getAllVehicles();
            verify(userVehicleRepository, never()).save(any());
        }

        @Test
        @DisplayName("should assign random vehicle when user has no existing assignment")
        void shouldAssignRandomVehicleForNewUser() {
            // Given
            when(userVehicleRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

            List<VehicleDto> availableVehicles = List.of(
                    new VehicleDto(1, "Furari", "SPORTS", 670, new BigDecimal("245000.00"), "furari-hero", "/models/furarri.glb"),
                    new VehicleDto(2, "GTR", "SPORTS", 565, new BigDecimal("115000.00"), "gtr-hero", "/models/GTR.glb")
            );
            when(vehicleServiceClient.getAllVehicles()).thenReturn(availableVehicles);

            when(userVehicleRepository.save(any(UserVehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            UserVehicle result = assignmentService.getOrAssignVehicle(USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(USER_ID);
            assertThat(result.getVehicleName()).isIn("Furari", "GTR");
            assertThat(result.getMaintenanceDueDate()).isEqualTo(LocalDate.now().plusMonths(6));
            verify(userVehicleRepository).findByUserId(USER_ID);
            verify(vehicleServiceClient).getAllVehicles();
            verify(userVehicleRepository).save(any(UserVehicle.class));
        }

        @Test
        @DisplayName("should throw exception when no vehicles available")
        void shouldThrowExceptionWhenNoVehiclesAvailable() {
            // Given
            when(userVehicleRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
            when(vehicleServiceClient.getAllVehicles()).thenReturn(Collections.emptyList());

            // When/Then
            assertThatThrownBy(() -> assignmentService.getOrAssignVehicle(USER_ID))
                    .isInstanceOf(VehicleServiceException.class)
                    .hasMessageContaining("No vehicles available");

            verify(userVehicleRepository, never()).save(any());
        }

        @Test
        @DisplayName("should set maintenance date to 6 months from now")
        void shouldSetMaintenanceDateSixMonthsAhead() {
            // Given
            when(userVehicleRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

            List<VehicleDto> availableVehicles = List.of(
                    new VehicleDto(1, "Furari", "SPORTS", 670, new BigDecimal("245000.00"), "furari-hero", "/models/furarri.glb")
            );
            when(vehicleServiceClient.getAllVehicles()).thenReturn(availableVehicles);
            when(userVehicleRepository.save(any(UserVehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            UserVehicle result = assignmentService.getOrAssignVehicle(USER_ID);

            // Then
            LocalDate expectedDate = LocalDate.now().plusMonths(6);
            assertThat(result.getMaintenanceDueDate()).isEqualTo(expectedDate);
        }
    }
}

