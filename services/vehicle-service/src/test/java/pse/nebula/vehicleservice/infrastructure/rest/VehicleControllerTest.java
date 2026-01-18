package pse.nebula.vehicleservice.infrastructure.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pse.nebula.vehicleservice.application.service.ConfigurationService;
import pse.nebula.vehicleservice.application.service.ConfigurationService.VehicleConfiguration;
import pse.nebula.vehicleservice.application.service.VehicleService;
import pse.nebula.vehicleservice.domain.exception.VehicleNotFoundException;
import pse.nebula.vehicleservice.domain.model.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
@DisplayName("VehicleController Integration Tests")
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleService vehicleService;

    @MockitoBean
    private ConfigurationService configurationService;

    @Nested
    @DisplayName("GET /api/v1/vehicles")
    class GetAllVehiclesTests {

        @Test
        @DisplayName("should return 200 with list of vehicles")
        void shouldReturnVehiclesList() throws Exception {
            // Given
            Vehicle vehicle = new Vehicle("Furari", CarType.SPORTS, 670, new BigDecimal("245000.00"), "furarri-hero", "/models/furarri.glb");
            Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle));
            when(vehicleService.getAllVehicles(any(Pageable.class))).thenReturn(vehiclePage);

            // When/Then
            mockMvc.perform(get("/api/v1/vehicles")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.vehicles", hasSize(1)))
                    .andExpect(jsonPath("$.vehicles[0].carName", is("Furari")))
                    .andExpect(jsonPath("$.vehicles[0].carType", is("SPORTS")))
                    .andExpect(jsonPath("$.vehicles[0].horsePower", is(670)))
                    .andExpect(jsonPath("$.vehicles[0].basePrice", is(245000.00)))
                    .andExpect(jsonPath("$.vehicles[0].image", is("furarri-hero")))
                    .andExpect(jsonPath("$.vehicles[0].modelPath", is("/models/furarri.glb")));
        }

        @Test
        @DisplayName("should return 200 with empty list when no vehicles")
        void shouldReturnEmptyList() throws Exception {
            // Given
            Page<Vehicle> emptyPage = new PageImpl<>(Collections.emptyList());
            when(vehicleService.getAllVehicles(any(Pageable.class))).thenReturn(emptyPage);

            // When/Then
            mockMvc.perform(get("/api/v1/vehicles")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.vehicles", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/vehicles/{id}")
    class GetVehicleByIdTests {

        @Test
        @DisplayName("should return 200 with vehicle details")
        void shouldReturnVehicle() throws Exception {
            // Given
            Vehicle vehicle = new Vehicle("Dacia", CarType.SEDAN, 150, new BigDecimal("22000.00"), "dacia-hero", "/models/Dacia.glb");
            when(vehicleService.getVehicleById(1)).thenReturn(vehicle);

            // When/Then
            mockMvc.perform(get("/api/v1/vehicles/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.carName", is("Dacia")))
                    .andExpect(jsonPath("$.carType", is("SEDAN")))
                    .andExpect(jsonPath("$.horsePower", is(150)))
                    .andExpect(jsonPath("$.modelPath", is("/models/Dacia.glb")));
        }

        @Test
        @DisplayName("should return 404 when vehicle not found")
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            when(vehicleService.getVehicleById(999)).thenThrow(new VehicleNotFoundException(999));

            // When/Then
            mockMvc.perform(get("/api/v1/vehicles/999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not Found")))
                    .andExpect(jsonPath("$.message", containsString("999")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/vehicles/{id}/configuration")
    class GetVehicleConfigurationTests {

        @Test
        @DisplayName("should return 200 with configuration options")
        void shouldReturnConfiguration() throws Exception {
            // Given
            Vehicle vehicle = new Vehicle("Furari", CarType.SPORTS, 670, new BigDecimal("245000.00"), "furarri-hero", "/models/furarri.glb");

            Paint paint = new Paint("Black", "Timeless deep black metallic finish", "black", "#000000");
            paint.addPrice(new PaintPrice(CarType.SPORTS, new BigDecimal("500.00")));

            Rim rim = new Rim("Sport 19\"", "Standard wheels", "rim-19-base", "sport");
            rim.addPrice(new RimPrice(CarType.SPORTS, BigDecimal.ZERO));

            Interior interior = new Interior("Black Leather", "Classic black leather", "interior-black", "black");
            interior.addPrice(new InteriorPrice(CarType.SPORTS, BigDecimal.ZERO));

            VehicleConfiguration config = new VehicleConfiguration(vehicle, List.of(paint), List.of(rim), List.of(interior), CarType.SPORTS);
            when(configurationService.getConfigurationForVehicle(1)).thenReturn(config);

            // When/Then - verify category-based structure
            mockMvc.perform(get("/api/v1/vehicles/1/configuration")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Furari")))
                    .andExpect(jsonPath("$.modelPath", is("/models/furarri.glb")))
                    .andExpect(jsonPath("$.basePrice", is(245000)))
                    .andExpect(jsonPath("$.categories", hasSize(3)))
                    .andExpect(jsonPath("$.categories[0].id", is("paint")))
                    .andExpect(jsonPath("$.categories[0].name", is("Exterior Color")))
                    .andExpect(jsonPath("$.categories[0].parts", hasSize(1)))
                    .andExpect(jsonPath("$.categories[0].parts[0].name", is("Black")))
                    .andExpect(jsonPath("$.categories[0].parts[0].cost", is(500)))
                    .andExpect(jsonPath("$.categories[0].parts[0].visualKey", is("black")))
                    .andExpect(jsonPath("$.categories[0].parts[0].hex", is("#000000")))
                    .andExpect(jsonPath("$.categories[1].id", is("rims")))
                    .andExpect(jsonPath("$.categories[1].parts[0].name", is("Sport 19\"")))
                    .andExpect(jsonPath("$.categories[1].parts[0].cost", is(0)))
                    .andExpect(jsonPath("$.categories[2].id", is("interior")))
                    .andExpect(jsonPath("$.categories[2].parts[0].name", is("Black Leather")));
        }

        @Test
        @DisplayName("should return 404 when vehicle not found")
        void shouldReturn404WhenVehicleNotFound() throws Exception {
            // Given
            when(configurationService.getConfigurationForVehicle(999))
                    .thenThrow(new VehicleNotFoundException(999));

            // When/Then
            mockMvc.perform(get("/api/v1/vehicles/999/configuration")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", containsString("999")));
        }
    }
}
