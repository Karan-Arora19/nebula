package pse.nebula.uservehicleservice.infrastructure.adapter.outbound.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleTelemetryPublisher Unit Tests")
class VehicleTelemetryPublisherTest {

    private static final String TOPIC_PREFIX = "nebula/user";
    private static final String USER_ID = "user-123";
    private static final String VEHICLE_NAME = "Furari";

    @Mock
    private Mqtt5AsyncClient mqttClient;

    @Mock
    private Mqtt5PublishBuilder.Send sendBuilder;

    @Mock
    private Mqtt5PublishBuilder.Send.Complete<CompletableFuture<Mqtt5Publish>> completeBuilder;

    private ObjectMapper objectMapper;
    private VehicleTelemetryPublisher telemetryPublisher;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        telemetryPublisher = new VehicleTelemetryPublisher(mqttClient, objectMapper, TOPIC_PREFIX);
    }

    @Test
    @DisplayName("should start publishing for new user")
    void shouldStartPublishingForNewUser() {
        // Given
        setupMqttClientMock();

        // When
        telemetryPublisher.startPublishing(USER_ID, VEHICLE_NAME);

        // Then
        assertThat(telemetryPublisher.isPublishing(USER_ID)).isTrue();
        assertThat(telemetryPublisher.getActivePublisherCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("should not duplicate publishing for same user")
    void shouldNotDuplicatePublishing() {
        // Given
        setupMqttClientMock();

        // When
        telemetryPublisher.startPublishing(USER_ID, VEHICLE_NAME);
        telemetryPublisher.startPublishing(USER_ID, VEHICLE_NAME);

        // Then
        assertThat(telemetryPublisher.getActivePublisherCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("should stop publishing for user")
    void shouldStopPublishing() {
        // Given
        setupMqttClientMock();
        telemetryPublisher.startPublishing(USER_ID, VEHICLE_NAME);

        // When
        telemetryPublisher.stopPublishing(USER_ID);

        // Then
        assertThat(telemetryPublisher.isPublishing(USER_ID)).isFalse();
        assertThat(telemetryPublisher.getActivePublisherCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("should publish to correct topic")
    void shouldPublishToCorrectTopic() {
        // Given
        setupMqttClientMock();

        // When
        telemetryPublisher.startPublishing(USER_ID, VEHICLE_NAME);

        // Then - verify topic contains user ID
        verify(sendBuilder).topic("nebula/user/user-123/vehicle/info");
    }

    @Test
    @DisplayName("should handle multiple users")
    void shouldHandleMultipleUsers() {
        // Given
        setupMqttClientMock();

        // When
        telemetryPublisher.startPublishing("user-1", "Furari");
        telemetryPublisher.startPublishing("user-2", "GTR");
        telemetryPublisher.startPublishing("user-3", "P-911");

        // Then
        assertThat(telemetryPublisher.getActivePublisherCount()).isEqualTo(3);
        assertThat(telemetryPublisher.isPublishing("user-1")).isTrue();
        assertThat(telemetryPublisher.isPublishing("user-2")).isTrue();
        assertThat(telemetryPublisher.isPublishing("user-3")).isTrue();
    }

    @Test
    @DisplayName("should cleanup on shutdown")
    void shouldCleanupOnShutdown() {
        // Given
        setupMqttClientMock();
        telemetryPublisher.startPublishing("user-1", "Furari");
        telemetryPublisher.startPublishing("user-2", "GTR");

        // When
        telemetryPublisher.shutdown();

        // Then
        assertThat(telemetryPublisher.getActivePublisherCount()).isEqualTo(0);
    }

    @SuppressWarnings("unchecked")
    private void setupMqttClientMock() {

        when(mqttClient.publishWith()).thenReturn(sendBuilder);
        when(sendBuilder.topic(anyString())).thenReturn(completeBuilder);
        when(completeBuilder.payload(any(byte[].class))).thenReturn(completeBuilder);
        when(completeBuilder.send()).thenReturn(CompletableFuture.completedFuture(mock(Mqtt5Publish.class)));
    }
}

