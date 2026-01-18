package pse.nebula.merchandise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MerchandiseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MerchandiseServiceApplication.class, args);
    }
}
