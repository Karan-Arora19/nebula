package pse.nebula.uservehicleservice.infrastructure.adapter.inbound.rest.dto;

import pse.nebula.uservehicleservice.domain.model.UserVehicle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Response DTO for user vehicle info endpoint.
 * Contains maintenance date and tyre pressure information.
 */
public record UserVehicleInfoResponse(
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate maintenanceDueDate,
                TyrePressures tyrePressures) {
        private static final double MIN_TYRE_PRESSURE = 28.0;
        private static final double MAX_TYRE_PRESSURE = 35.0;
        private static final Random RANDOM = new Random();

        /**
         * DTO representing all four tyre pressures.
         */
        public record TyrePressures(
                        BigDecimal frontLeft,
                        BigDecimal frontRight,
                        BigDecimal rearLeft,
                        BigDecimal rearRight) {
        }

        /**
         * Creates a response DTO from a UserVehicle entity.
         * Tyre pressures are generated randomly on each call.
         *
         * @param userVehicle the user vehicle entity
         * @return the response DTO
         */
        public static UserVehicleInfoResponse fromEntity(UserVehicle userVehicle) {
                return new UserVehicleInfoResponse(
                                userVehicle.getMaintenanceDueDate(),
                                new TyrePressures(
                                                generateRandomTyrePressure(),
                                                generateRandomTyrePressure(),
                                                generateRandomTyrePressure(),
                                                generateRandomTyrePressure()));
        }

        /**
         * Generates a random tyre pressure between 28.0 and 35.0 PSI.
         *
         * @return random tyre pressure with one decimal place
         */
        private static BigDecimal generateRandomTyrePressure() {
                double pressure = MIN_TYRE_PRESSURE + (RANDOM.nextDouble() * (MAX_TYRE_PRESSURE - MIN_TYRE_PRESSURE));
                return BigDecimal.valueOf(pressure).setScale(1, RoundingMode.HALF_UP);
        }
}
