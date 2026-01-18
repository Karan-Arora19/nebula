package pse.nebula.vehicleservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.config.import=",
    "spring.cloud.config.enabled=false",
    "eureka.client.enabled=false"
})
class VehicleServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring application context loads successfully
    }

}

