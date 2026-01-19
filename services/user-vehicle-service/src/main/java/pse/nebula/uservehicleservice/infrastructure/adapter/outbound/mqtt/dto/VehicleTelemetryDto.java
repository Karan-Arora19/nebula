package pse.nebula.uservehicleservice.infrastructure.adapter.outbound.mqtt.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO representing vehicle telemetry data published to MQTT.
 */
public record VehicleTelemetryDto(
        String vehicleName,
        LocationDto location,
        BigDecimal fuel,
        Instant timestamp
) {
    /**
     * DTO representing vehicle location coordinates.
     */
    public record LocationDto(
            double lat,
            double lng
    ) {
    }
}

