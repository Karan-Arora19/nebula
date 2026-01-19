package pse.nebula.uservehicleservice.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Entity representing the assignment of a vehicle to a user.
 * Stores user-vehicle mapping along with maintenance data.
 */
@Entity
@Table(name = "user_vehicle", schema = "user_vehicle_service")
@Getter
@NoArgsConstructor
public class UserVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "vehicle_id", nullable = false)
    private Integer vehicleId;

    @Column(name = "vehicle_name", nullable = false)
    private String vehicleName;

    @Column(name = "maintenance_due_date", nullable = false)
    private LocalDate maintenanceDueDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Creates a new user-vehicle assignment with maintenance data.
     *
     * @param userId             the unique identifier of the user
     * @param vehicleId          the ID of the assigned vehicle from vehicle-service
     * @param vehicleName        the name of the assigned vehicle
     * @param maintenanceDueDate the next maintenance due date
     */
    public UserVehicle(String userId, Integer vehicleId, String vehicleName, LocalDate maintenanceDueDate) {
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.maintenanceDueDate = maintenanceDueDate;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "UserVehicle{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", vehicleId=" + vehicleId +
                ", vehicleName='" + vehicleName + '\'' +
                ", maintenanceDueDate=" + maintenanceDueDate +
                '}';
    }
}

