package pse.nebula.uservehicleservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.mqtt.VehicleTelemetryPublisher;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.VehicleServiceClient;

@SpringBootTest
@ActiveProfiles("test")
class UserVehicleServiceApplicationTests {

    @MockitoBean
    private VehicleServiceClient vehicleServiceClient;

    @MockitoBean
    private VehicleTelemetryPublisher telemetryPublisher;

    @Test
    void contextLoads() {
        // Verify application context loads successfully
    }
}

