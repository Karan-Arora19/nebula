package pse.nebula.uservehicleservice.infrastructure.adapter.outbound.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.mqtt.dto.VehicleTelemetryDto;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;

/**
 * MQTT Publisher for vehicle telemetry data.
 * Publishes vehicle location and fuel data to user-specific MQTT topics.
 * Manages scheduled publishing every 5 minutes per active user.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "mqtt.enabled", havingValue = "true", matchIfMissing = true)
public class VehicleTelemetryPublisher {

    private static final long PUBLISH_INTERVAL_MINUTES = 5;

    // Fixed location (Stuttgart area)
    private static final double FIXED_LATITUDE = 48.7758;
    private static final double FIXED_LONGITUDE = 9.1829;

    // Fixed fuel level
    private static final BigDecimal FIXED_FUEL = BigDecimal.valueOf(75.0);

    private final Mqtt5AsyncClient mqttClient;
    private final ObjectMapper objectMapper;
    private final String topicPrefix;
    private final ScheduledExecutorService scheduler;
    private final Map<String, ScheduledFuture<?>> activePublishers;

    public VehicleTelemetryPublisher(
            Mqtt5AsyncClient mqttClient,
            ObjectMapper objectMapper,
            @Value("${mqtt.topic.prefix:nebula/user}") String topicPrefix) {
        this.mqttClient = mqttClient;
        this.objectMapper = objectMapper;
        this.topicPrefix = topicPrefix;
        this.scheduler = Executors.newScheduledThreadPool(4, r -> {
            Thread thread = new Thread(r, "mqtt-telemetry-publisher");
            thread.setDaemon(true);
            return thread;
        });
        this.activePublishers = new ConcurrentHashMap<>();
        log.info("VehicleTelemetryPublisher initialized with topic prefix: {}", topicPrefix);
    }

    /**
     * Starts publishing telemetry for a user's vehicle.
     * If already publishing for this user, does nothing.
     *
     * @param userId      the user ID
     * @param vehicleName the name of the vehicle
     */
    public void startPublishing(String userId, String vehicleName) {
        if (activePublishers.containsKey(userId)) {
            log.debug("Already publishing telemetry for user: {}", userId);
            return;
        }

        log.info("Starting telemetry publishing for user: {} with vehicle: {}", userId, vehicleName);

        // Publish immediately on first call
        publishTelemetry(userId, vehicleName);

        // Schedule periodic publishing every 5 minutes
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                () -> publishTelemetry(userId, vehicleName),
                PUBLISH_INTERVAL_MINUTES,
                PUBLISH_INTERVAL_MINUTES,
                TimeUnit.MINUTES
        );

        activePublishers.put(userId, future);
        log.info("Scheduled telemetry publishing for user: {} every {} minutes", userId, PUBLISH_INTERVAL_MINUTES);
    }

    /**
     * Stops publishing telemetry for a specific user.
     *
     * @param userId the user ID
     */
    public void stopPublishing(String userId) {
        ScheduledFuture<?> future = activePublishers.remove(userId);
        if (future != null) {
            future.cancel(false);
            log.info("Stopped telemetry publishing for user: {}", userId);
        }
    }

    /**
     * Publishes telemetry data to the user's MQTT topic.
     *
     * @param userId      the user ID
     * @param vehicleName the name of the vehicle
     */
    private void publishTelemetry(String userId, String vehicleName) {
        String topic = buildTopic(userId);

        VehicleTelemetryDto telemetry = new VehicleTelemetryDto(
                vehicleName,
                new VehicleTelemetryDto.LocationDto(FIXED_LATITUDE, FIXED_LONGITUDE),
                FIXED_FUEL,
                Instant.now()
        );

        try {
            String payload = objectMapper.writeValueAsString(telemetry);

            mqttClient.publishWith()
                    .topic(topic)
                    .payload(payload.getBytes(StandardCharsets.UTF_8))
                    .send()
                    .whenComplete((publish, throwable) -> {
                        if (throwable != null) {
                            log.error("Failed to publish telemetry to topic {}: {}", topic, throwable.getMessage());
                        } else {
                            log.debug("Published telemetry to topic: {} - {}", topic, payload);
                        }
                    });

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize telemetry data for user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Builds the MQTT topic for a user.
     *
     * @param userId the user ID
     * @return the full topic path
     */
    private String buildTopic(String userId) {
        return topicPrefix + "/" + userId + "/vehicle/info";
    }

    /**
     * Checks if publishing is active for a user.
     *
     * @param userId the user ID
     * @return true if publishing is active
     */
    public boolean isPublishing(String userId) {
        return activePublishers.containsKey(userId);
    }

    /**
     * Gets the count of active publishers.
     *
     * @return the number of active publishers
     */
    public int getActivePublisherCount() {
        return activePublishers.size();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down VehicleTelemetryPublisher with {} active publishers", activePublishers.size());

        // Cancel all scheduled tasks
        activePublishers.values().forEach(future -> future.cancel(false));
        activePublishers.clear();

        // Shutdown scheduler
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        log.info("VehicleTelemetryPublisher shutdown complete");
    }
}

