package pse.nebula.uservehicleservice.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MQTT Configuration for RabbitMQ messaging.
 * Connects to RabbitMQ's MQTT plugin for publishing vehicle telemetry updates.
 */
@Slf4j
@Configuration
public class MqttConfig {

    private static final int CONNECTION_TIMEOUT_SECONDS = 10;

    @Value("${mqtt.broker.host:localhost}")
    private String brokerHost;

    @Value("${mqtt.broker.port:1883}")
    private int brokerPort;

    @Value("${mqtt.client.id:user-vehicle-service}")
    private String clientIdPrefix;

    @Value("${mqtt.username:nebula}")
    private String username;

    @Value("${mqtt.password:nebula@2025}")
    private String password;

    @Bean
    @ConditionalOnProperty(name = "mqtt.enabled", havingValue = "true", matchIfMissing = true)
    public Mqtt5AsyncClient mqttClient() {
        String clientId = clientIdPrefix + "-" + UUID.randomUUID().toString().substring(0, 8);

        log.info("Connecting to MQTT broker at {}:{} with client ID: {}", brokerHost, brokerPort, clientId);

        Mqtt5AsyncClient client = Mqtt5Client.builder()
                .identifier(clientId)
                .serverHost(brokerHost)
                .serverPort(brokerPort)
                .automaticReconnectWithDefaultConfig()
                .build()
                .toAsync();

        // Connect with authentication - wait for connection to complete
        try {
            client.connectWith()
                    .simpleAuth()
                    .username(username)
                    .password(password.getBytes())
                    .applySimpleAuth()
                    .send()
                    .get(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            log.info("Successfully connected to MQTT broker at {}:{}", brokerHost, brokerPort);
        } catch (Exception e) {
            log.error("Failed to connect to MQTT broker: {}. Publishing will be disabled.", e.getMessage());
        }

        return client;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }
}

