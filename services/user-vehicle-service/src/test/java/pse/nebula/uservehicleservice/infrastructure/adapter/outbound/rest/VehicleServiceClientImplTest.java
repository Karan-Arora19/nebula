package pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.dto.VehicleDto;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.dto.VehiclesOverviewResponse;
import pse.nebula.uservehicleservice.infrastructure.exception.VehicleServiceException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleServiceClient Unit Tests")
class VehicleServiceClientImplTest {

    private static final String BASE_URL = "http://localhost:8081";
    private static final String VEHICLES_ENDPOINT = "/api/v1/vehicles";

    @Mock
    private RestTemplate restTemplate;

    private VehicleServiceClientImpl vehicleServiceClient;

    @BeforeEach
    void setUp() {
        vehicleServiceClient = new VehicleServiceClientImpl(restTemplate, BASE_URL, VEHICLES_ENDPOINT);
    }

    @Nested
    @DisplayName("getAllVehicles")
    class GetAllVehiclesTests {

        @Test
        @DisplayName("should return list of vehicles on successful response")
        void shouldReturnVehiclesList() {
            // Given
            List<VehicleDto> vehicles = List.of(
                    new VehicleDto(1, "Furari", "SPORTS", 670, new BigDecimal("245000.00"), "furari-hero", "/models/furarri.glb"),
                    new VehicleDto(2, "GTR", "SPORTS", 565, new BigDecimal("115000.00"), "gtr-hero", "/models/GTR.glb")
            );
            VehiclesOverviewResponse response = new VehiclesOverviewResponse(vehicles, 0, 1, 2, 100, false, false);

            when(restTemplate.getForObject(BASE_URL + VEHICLES_ENDPOINT + "?size=100", VehiclesOverviewResponse.class))
                    .thenReturn(response);

            // When
            List<VehicleDto> result = vehicleServiceClient.getAllVehicles();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).carName()).isEqualTo("Furari");
            assertThat(result.get(1).carName()).isEqualTo("GTR");
        }

        @Test
        @DisplayName("should return empty list when response is null")
        void shouldReturnEmptyListWhenResponseIsNull() {
            // Given
            when(restTemplate.getForObject(BASE_URL + VEHICLES_ENDPOINT + "?size=100", VehiclesOverviewResponse.class))
                    .thenReturn(null);

            // When
            List<VehicleDto> result = vehicleServiceClient.getAllVehicles();

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should throw VehicleServiceException on HTTP error")
        void shouldThrowExceptionOnHttpError() {
            // Given
            when(restTemplate.getForObject(BASE_URL + VEHICLES_ENDPOINT + "?size=100", VehiclesOverviewResponse.class))
                    .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

            // When/Then
            assertThatThrownBy(() -> vehicleServiceClient.getAllVehicles())
                    .isInstanceOf(VehicleServiceException.class)
                    .hasMessageContaining("Failed to fetch vehicles");
        }

        @Test
        @DisplayName("should throw VehicleServiceException when service unavailable")
        void shouldThrowExceptionWhenServiceUnavailable() {
            // Given
            when(restTemplate.getForObject(BASE_URL + VEHICLES_ENDPOINT + "?size=100", VehiclesOverviewResponse.class))
                    .thenThrow(new RestClientException("Connection refused"));

            // When/Then
            assertThatThrownBy(() -> vehicleServiceClient.getAllVehicles())
                    .isInstanceOf(VehicleServiceException.class)
                    .hasMessageContaining("Vehicle service unavailable");
        }
    }

    @Nested
    @DisplayName("getVehicleById")
    class GetVehicleByIdTests {

        @Test
        @DisplayName("should return vehicle when found")
        void shouldReturnVehicleWhenFound() {
            // Given
            VehicleDto vehicle = new VehicleDto(1, "Furari", "SPORTS", 670, new BigDecimal("245000.00"), "furari-hero", "/models/furarri.glb");

            when(restTemplate.getForObject(BASE_URL + VEHICLES_ENDPOINT + "/1", VehicleDto.class))
                    .thenReturn(vehicle);

            // When
            Optional<VehicleDto> result = vehicleServiceClient.getVehicleById(1);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().carName()).isEqualTo("Furari");
        }

        @Test
        @DisplayName("should return empty when vehicle not found")
        void shouldReturnEmptyWhenNotFound() {
            // Given
            when(restTemplate.getForObject(BASE_URL + VEHICLES_ENDPOINT + "/999", VehicleDto.class))
                    .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

            // When
            Optional<VehicleDto> result = vehicleServiceClient.getVehicleById(999);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should throw VehicleServiceException on HTTP error")
        void shouldThrowExceptionOnHttpError() {
            // Given
            when(restTemplate.getForObject(BASE_URL + VEHICLES_ENDPOINT + "/1", VehicleDto.class))
                    .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

            // When/Then
            assertThatThrownBy(() -> vehicleServiceClient.getVehicleById(1))
                    .isInstanceOf(VehicleServiceException.class)
                    .hasMessageContaining("Failed to fetch vehicle");
        }
    }
}

