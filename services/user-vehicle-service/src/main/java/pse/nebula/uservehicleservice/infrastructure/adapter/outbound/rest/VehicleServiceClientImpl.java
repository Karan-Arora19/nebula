package pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.dto.VehicleDto;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.dto.VehiclesOverviewResponse;
import pse.nebula.uservehicleservice.infrastructure.exception.VehicleServiceException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * REST client implementation for communicating with vehicle-service.
 * Handles HTTP requests and error handling for vehicle data retrieval.
 */
@Slf4j
@Component
public class VehicleServiceClientImpl implements VehicleServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String vehiclesEndpoint;

    public VehicleServiceClientImpl(
            RestTemplate restTemplate,
            @Value("${vehicle-service.base-url}") String baseUrl,
            @Value("${vehicle-service.endpoints.vehicles}") String vehiclesEndpoint) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.vehiclesEndpoint = vehiclesEndpoint;
        log.info("VehicleServiceClient initialized with baseUrl: {}", baseUrl);
    }

    @Override
    public List<VehicleDto> getAllVehicles() {
        String url = baseUrl + vehiclesEndpoint + "?size=100";
        log.debug("Fetching all vehicles from: {}", url);

        try {
            VehiclesOverviewResponse response = restTemplate.getForObject(url, VehiclesOverviewResponse.class);

            if (response == null || response.vehicles() == null) {
                log.warn("Received null or empty response from vehicle-service");
                return Collections.emptyList();
            }

            log.info("Successfully fetched {} vehicles from vehicle-service", response.vehicles().size());
            return response.vehicles();

        } catch (HttpClientErrorException e) {
            log.error("HTTP error while fetching vehicles: {} - {}", e.getStatusCode(), e.getMessage());
            throw new VehicleServiceException("Failed to fetch vehicles: " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new VehicleServiceException("Vehicle service unavailable: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<VehicleDto> getVehicleById(Integer vehicleId) {
        String url = baseUrl + vehiclesEndpoint + "/" + vehicleId;
        log.debug("Fetching vehicle by ID from: {}", url);

        try {
            VehicleDto vehicle = restTemplate.getForObject(url, VehicleDto.class);

            if (vehicle == null) {
                log.warn("Vehicle with ID {} not found", vehicleId);
                return Optional.empty();
            }

            log.debug("Successfully fetched vehicle: {}", vehicle.carName());
            return Optional.of(vehicle);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Vehicle with ID {} not found in vehicle-service", vehicleId);
                return Optional.empty();
            }
            log.error("HTTP error while fetching vehicle {}: {} - {}", vehicleId, e.getStatusCode(), e.getMessage());
            throw new VehicleServiceException("Failed to fetch vehicle: " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new VehicleServiceException("Vehicle service unavailable: " + e.getMessage(), e);
        }
    }
}

