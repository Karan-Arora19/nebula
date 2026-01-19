package pse.nebula.uservehicleservice.infrastructure.adapter.inbound.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pse.nebula.uservehicleservice.application.service.UserVehicleAssignmentService;
import pse.nebula.uservehicleservice.domain.model.UserVehicle;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.mqtt.VehicleTelemetryPublisher;
import pse.nebula.uservehicleservice.infrastructure.exception.VehicleServiceException;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserVehicleController.class)
@DisplayName("UserVehicleController Unit Tests")
class UserVehicleControllerTest {

    private static final String USER_VEHICLE_INFO_URL = "/api/v1/user-vehicle/info";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ID = "user-123";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserVehicleAssignmentService assignmentService;

    @MockitoBean
    private VehicleTelemetryPublisher telemetryPublisher;

    @Nested
    @DisplayName("GET /api/v1/user-vehicle/info")
    class GetUserVehicleInfoTests {

        @Test
        @DisplayName("should return 200 with vehicle info when user has assigned vehicle")
        void shouldReturnVehicleInfo() throws Exception {
            // Given
            LocalDate maintenanceDate = LocalDate.of(2026, 7, 18);
            UserVehicle userVehicle = new UserVehicle(USER_ID, 1, "Furari", maintenanceDate);
            when(assignmentService.getOrAssignVehicle(USER_ID)).thenReturn(userVehicle);

            // When/Then
            mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                            .header(USER_ID_HEADER, USER_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.maintenanceDueDate").value("2026-07-18"))
                    .andExpect(jsonPath("$.tyrePressures").exists())
                    .andExpect(jsonPath("$.tyrePressures.frontLeft").isNumber())
                    .andExpect(jsonPath("$.tyrePressures.frontRight").isNumber())
                    .andExpect(jsonPath("$.tyrePressures.rearLeft").isNumber())
                    .andExpect(jsonPath("$.tyrePressures.rearRight").isNumber());

            verify(assignmentService).getOrAssignVehicle(USER_ID);
            verify(telemetryPublisher).startPublishing(USER_ID, "Furari");
        }

        @Test
        @DisplayName("should return tyre pressures within valid range (28-35 PSI)")
        void shouldReturnTyrePressuresInValidRange() throws Exception {
            // Given
            LocalDate maintenanceDate = LocalDate.of(2026, 7, 18);
            UserVehicle userVehicle = new UserVehicle(USER_ID, 1, "Furari", maintenanceDate);
            when(assignmentService.getOrAssignVehicle(USER_ID)).thenReturn(userVehicle);

            // When/Then
            mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                            .header(USER_ID_HEADER, USER_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tyrePressures.frontLeft", allOf(greaterThanOrEqualTo(28.0), lessThanOrEqualTo(35.0))))
                    .andExpect(jsonPath("$.tyrePressures.frontRight", allOf(greaterThanOrEqualTo(28.0), lessThanOrEqualTo(35.0))))
                    .andExpect(jsonPath("$.tyrePressures.rearLeft", allOf(greaterThanOrEqualTo(28.0), lessThanOrEqualTo(35.0))))
                    .andExpect(jsonPath("$.tyrePressures.rearRight", allOf(greaterThanOrEqualTo(28.0), lessThanOrEqualTo(35.0))));
        }

        @Test
        @DisplayName("should return 400 when X-User-Id header is missing")
        void shouldReturn400WhenUserIdMissing() throws Exception {
            // When/Then
            mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value(containsString("X-User-Id")));

            verify(assignmentService, never()).getOrAssignVehicle(anyString());
            verify(telemetryPublisher, never()).startPublishing(anyString(), anyString());
        }

        @Test
        @DisplayName("should return 503 when vehicle service is unavailable")
        void shouldReturn503WhenVehicleServiceUnavailable() throws Exception {
            // Given
            when(assignmentService.getOrAssignVehicle(USER_ID))
                    .thenThrow(new VehicleServiceException("Vehicle service unavailable"));

            // When/Then
            mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                            .header(USER_ID_HEADER, USER_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.status").value(503))
                    .andExpect(jsonPath("$.message").value(containsString("Vehicle service unavailable")));

            verify(telemetryPublisher, never()).startPublishing(anyString(), anyString());
        }

        @Test
        @DisplayName("should start MQTT publishing on successful request")
        void shouldStartMqttPublishing() throws Exception {
            // Given
            LocalDate maintenanceDate = LocalDate.of(2026, 7, 18);
            UserVehicle userVehicle = new UserVehicle(USER_ID, 1, "GTR", maintenanceDate);
            when(assignmentService.getOrAssignVehicle(USER_ID)).thenReturn(userVehicle);

            // When
            mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                            .header(USER_ID_HEADER, USER_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Then
            verify(telemetryPublisher).startPublishing(USER_ID, "GTR");
        }
    }
}

