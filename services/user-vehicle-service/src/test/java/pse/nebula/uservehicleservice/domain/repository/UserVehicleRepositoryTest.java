package pse.nebula.uservehicleservice.domain.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pse.nebula.uservehicleservice.domain.model.UserVehicle;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserVehicleRepository Integration Tests")
class UserVehicleRepositoryTest {

    @Autowired
    private UserVehicleRepository userVehicleRepository;

    @Test
    @DisplayName("should save and find user vehicle by user ID")
    void shouldSaveAndFindByUserId() {
        // Given
        UserVehicle userVehicle = new UserVehicle(
                "user-123",
                1,
                "Furari",
                LocalDate.now().plusMonths(6)
        );

        // When
        userVehicleRepository.save(userVehicle);
        Optional<UserVehicle> found = userVehicleRepository.findByUserId("user-123");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo("user-123");
        assertThat(found.get().getVehicleId()).isEqualTo(1);
        assertThat(found.get().getVehicleName()).isEqualTo("Furari");
    }

    @Test
    @DisplayName("should return empty when user not found")
    void shouldReturnEmptyWhenUserNotFound() {
        // When
        Optional<UserVehicle> found = userVehicleRepository.findByUserId("non-existent-user");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should check if user exists by user ID")
    void shouldCheckExistsByUserId() {
        // Given
        UserVehicle userVehicle = new UserVehicle(
                "user-456",
                2,
                "GTR",
                LocalDate.now().plusMonths(6)
        );
        userVehicleRepository.save(userVehicle);

        // When/Then
        assertThat(userVehicleRepository.existsByUserId("user-456")).isTrue();
        assertThat(userVehicleRepository.existsByUserId("non-existent")).isFalse();
    }

    @Test
    @DisplayName("should persist timestamps on save")
    void shouldPersistTimestamps() {
        // Given
        UserVehicle userVehicle = new UserVehicle(
                "user-789",
                3,
                "P-911",
                LocalDate.now().plusMonths(6)
        );

        // When
        UserVehicle saved = userVehicleRepository.save(userVehicle);

        // Then
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}

