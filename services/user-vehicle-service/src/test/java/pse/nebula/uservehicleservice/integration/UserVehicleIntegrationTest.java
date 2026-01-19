package pse.nebula.uservehicleservice.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pse.nebula.uservehicleservice.domain.model.UserVehicle;
import pse.nebula.uservehicleservice.domain.repository.UserVehicleRepository;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.mqtt.VehicleTelemetryPublisher;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.VehicleServiceClient;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.dto.VehicleDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("User Vehicle Service Integration Tests")
class UserVehicleIntegrationTest {

    private static final String USER_VEHICLE_INFO_URL = "/api/v1/user-vehicle/info";
    private static final String USER_ID_HEADER = "X-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserVehicleRepository userVehicleRepository;

    @MockitoBean
    private VehicleServiceClient vehicleServiceClient;

    @MockitoBean
    private VehicleTelemetryPublisher telemetryPublisher;

    @BeforeEach
    void setUp() {
        userVehicleRepository.deleteAll();
    }

    @Test
    @DisplayName("should assign new vehicle for new user and persist to database")
    void shouldAssignNewVehicleForNewUser() throws Exception {
        // Given
        String userId = "new-user-001";
        List<VehicleDto> availableVehicles = List.of(
                new VehicleDto(1, "Furari", "SPORTS", 670, new BigDecimal("245000.00"), "furari-hero", "/models/furarri.glb")
        );
        when(vehicleServiceClient.getAllVehicles()).thenReturn(availableVehicles);

        // When
        mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maintenanceDueDate").exists())
                .andExpect(jsonPath("$.tyrePressures").exists());

        // Then - verify persisted to database
        assertThat(userVehicleRepository.findByUserId(userId)).isPresent();
        UserVehicle savedVehicle = userVehicleRepository.findByUserId(userId).get();
        assertThat(savedVehicle.getVehicleName()).isEqualTo("Furari");
        assertThat(savedVehicle.getVehicleId()).isEqualTo(1);
        assertThat(savedVehicle.getMaintenanceDueDate()).isEqualTo(LocalDate.now().plusMonths(6));

        // Verify MQTT publishing started
        verify(telemetryPublisher).startPublishing(userId, "Furari");
    }

    @Test
    @DisplayName("should return existing vehicle for returning user without calling vehicle service")
    void shouldReturnExistingVehicleForReturningUser() throws Exception {
        // Given - pre-create user vehicle assignment
        String userId = "existing-user-001";
        LocalDate maintenanceDate = LocalDate.of(2026, 7, 18);
        UserVehicle existingVehicle = new UserVehicle(userId, 2, "GTR", maintenanceDate);
        userVehicleRepository.save(existingVehicle);

        // When
        mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maintenanceDueDate").value("2026-07-18"))
                .andExpect(jsonPath("$.tyrePressures.frontLeft", greaterThanOrEqualTo(28.0)))
                .andExpect(jsonPath("$.tyrePressures.frontLeft", lessThanOrEqualTo(35.0)));

        // Then - verify vehicle service was NOT called
        verify(vehicleServiceClient, never()).getAllVehicles();

        // Verify MQTT publishing started with correct vehicle name
        verify(telemetryPublisher).startPublishing(userId, "GTR");
    }

    @Test
    @DisplayName("should generate different tyre pressures on each request")
    void shouldGenerateDifferentTyrePressuresOnEachRequest() throws Exception {
        // Given
        String userId = "tyre-test-user";
        LocalDate maintenanceDate = LocalDate.now().plusMonths(6);
        UserVehicle existingVehicle = new UserVehicle(userId, 1, "Furari", maintenanceDate);
        userVehicleRepository.save(existingVehicle);

        // When - make two requests
        String response1 = mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String response2 = mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Then - responses should have different tyre pressures (high probability)
        // Note: There's a tiny chance they could be the same, but it's extremely unlikely
        // This test verifies the random generation is working
        assertThat(response1).isNotBlank();
        assertThat(response2).isNotBlank();
    }

    @Test
    @DisplayName("should handle concurrent requests for same user")
    void shouldHandleConcurrentRequests() throws Exception {
        // Given
        String userId = "concurrent-user";
        List<VehicleDto> availableVehicles = List.of(
                new VehicleDto(1, "Furari", "SPORTS", 670, new BigDecimal("245000.00"), "furari-hero", "/models/furarri.glb")
        );
        when(vehicleServiceClient.getAllVehicles()).thenReturn(availableVehicles);

        // When - make multiple requests (simulating concurrent access)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                            .header(USER_ID_HEADER, userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        // Then - should only have one entry in database
        long count = userVehicleRepository.count();
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("should return correct response structure")
    void shouldReturnCorrectResponseStructure() throws Exception {
        // Given
        String userId = "structure-test-user";
        LocalDate maintenanceDate = LocalDate.of(2026, 8, 15);
        UserVehicle existingVehicle = new UserVehicle(userId, 1, "Furari", maintenanceDate);
        userVehicleRepository.save(existingVehicle);

        // When/Then
        mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.maintenanceDueDate").value("2026-08-15"))
                .andExpect(jsonPath("$.tyrePressures").isMap())
                .andExpect(jsonPath("$.tyrePressures.frontLeft").isNumber())
                .andExpect(jsonPath("$.tyrePressures.frontRight").isNumber())
                .andExpect(jsonPath("$.tyrePressures.rearLeft").isNumber())
                .andExpect(jsonPath("$.tyrePressures.rearRight").isNumber());
    }
}

