package pse.nebula.uservehicleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * User Vehicle Service Application.
 *
 * Manages user-vehicle assignments and publishes real-time vehicle telemetry
 * (location and fuel) via MQTT.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UserVehicleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserVehicleServiceApplication.class, args);
    }
}

