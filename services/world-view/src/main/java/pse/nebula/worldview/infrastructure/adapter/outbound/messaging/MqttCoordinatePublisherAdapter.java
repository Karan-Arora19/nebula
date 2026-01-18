package pse.nebula.worldview.infrastructure.adapter.outbound.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.port.outbound.CoordinatePublisher;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.dto.CoordinateUpdateDto;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.mapper.DtoMapper;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * MQTT-based adapter that implements the CoordinatePublisher outbound port.
 * Publishes coordinate updates to MQTT topics via RabbitMQ's MQTT plugin.
 * This allows frontend clients to subscribe via WebSocket MQTT.
 *
 * This is the PRIMARY publisher for real-time coordinate streaming.
 * Bean is created in MqttConfig with proper configuration.
 *
 * Topic structure:
 * - nebula/journey/{journeyId}/position - Real-time coordinate updates
 * - nebula/journey/{journeyId}/events - Journey lifecycle events (started, completed)
 */
@Slf4j
public class MqttCoordinatePublisherAdapter implements CoordinatePublisher {

    private static final int THREAD_POOL_SIZE = 4;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

    private final Mqtt5AsyncClient mqttClient;
    private final DtoMapper dtoMapper;
    private final ObjectMapper objectMapper;
    private final String topicPrefix;
    private final ExecutorService mqttExecutor;

    public MqttCoordinatePublisherAdapter(Mqtt5AsyncClient mqttClient, DtoMapper dtoMapper,
            ObjectMapper objectMapper, String topicPrefix) {
        this.mqttClient = mqttClient;
        this.dtoMapper = dtoMapper;
        this.objectMapper = objectMapper;
        this.topicPrefix = topicPrefix;
        this.mqttExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE, r -> {
            Thread thread = new Thread(r, "mqtt-publisher");
            thread.setDaemon(true);
            return thread;
        });
        log.info("MqttCoordinatePublisherAdapter initialized with {} threads", THREAD_POOL_SIZE);
    }

    /**
     * Gracefully shutdown the executor service.
     */
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down MQTT publisher executor...");
        mqttExecutor.shutdown();
        try {
            if (!mqttExecutor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                mqttExecutor.shutdownNow();
                log.warn("MQTT publisher executor did not terminate gracefully");
            }
        } catch (InterruptedException e) {
            mqttExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("MQTT publisher executor shutdown complete");
    }

    @Override
    public void publishCoordinateUpdate(String journeyId, Coordinate coordinate, JourneyState journeyState) {
        CoordinateUpdateDto update = dtoMapper.toCoordinateUpdate(journeyState);
        String topic = topicPrefix + "/" + journeyId + "/position";

        publishMessage(topic, update, "coordinate update");

        // MQTT publishing is silent - only log errors (handled in publishMessage)
        // Real-time updates are published continuously without logging noise
    }

    @Override
    public void publishJourneyStarted(JourneyState journeyState) {
        CoordinateUpdateDto update = dtoMapper.toCoordinateUpdate(journeyState);
        String topic = topicPrefix + "/" + journeyState.getJourneyId() + "/events";

        JourneyEventMessage event = new JourneyEventMessage("STARTED", update);
        publishMessage(topic, event, "journey started event");

        log.info("Published MQTT journey started event for: {} to topic: {}",
                journeyState.getJourneyId(), topic);
    }

    @Override
    public void publishJourneyCompleted(JourneyState journeyState) {
        CoordinateUpdateDto update = dtoMapper.toCoordinateUpdate(journeyState);
        String topic = topicPrefix + "/" + journeyState.getJourneyId() + "/events";

        JourneyEventMessage event = new JourneyEventMessage("COMPLETED", update);
        publishMessage(topic, event, "journey completed event");

        log.info("Published MQTT journey completed event for: {} to topic: {}",
                journeyState.getJourneyId(), topic);
    }

    private void publishMessage(String topic, Object payload, String messageType) {
        // Run MQTT publishing in executor thread pool to avoid blocking the main request thread
        // This ensures SSE works even if MQTT is slow or unavailable
        mqttExecutor.submit(() -> {
            try {
                String jsonPayload = objectMapper.writeValueAsString(payload);

                mqttClient.publishWith()
                        .topic(topic)
                        .payload(jsonPayload.getBytes(StandardCharsets.UTF_8))
                        .retain(false)
                        .send()
                        .whenComplete((publish, throwable) -> {
                            if (throwable != null) {
                                log.warn("Failed to publish {} to topic {}: {}",
                                        messageType, topic, throwable.getMessage());
                            }
                            // Success is silent - no logging for successful MQTT publishes
                        });
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize {} for MQTT: {}", messageType, e.getMessage());
            } catch (Exception e) {
                log.warn("MQTT publishing failed for {}: {}", messageType, e.getMessage());
            }
        });
    }

    /**
     * Wrapper for journey lifecycle events.
     */
    public record JourneyEventMessage(String eventType, CoordinateUpdateDto data) {}
}