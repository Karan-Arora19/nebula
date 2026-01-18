package pse.nebula.user.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pse.nebula.user.model.Role;
import pse.nebula.user.model.User;
import pse.nebula.user.repository.UserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.default.email:admin@nebula.com}")
    private String adminEmail;

    @Value("${admin.default.password:}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        // Check if any admin user exists
        Optional<User> existingAdmin = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .findFirst();

        if (existingAdmin.isEmpty()) {
            // Validate admin password is set via environment variable
            if (!StringUtils.hasText(adminPassword)) {
                log.error("❌ Cannot create admin user: ADMIN_DEFAULT_PASSWORD environment variable not set!");
                log.error("   Please set ADMIN_DEFAULT_PASSWORD environment variable or admin.default.password property");
                log.error("   Example: export ADMIN_DEFAULT_PASSWORD=your-secure-password");
                return;
            }

            log.info("No admin user found. Creating default admin user...");
            
            User admin = new User();
            admin.setUsername("admin");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);
            
            log.info("✅ Default admin user created successfully!");
            log.info("   Email: {}", adminEmail);
            log.info("   ⚠️  Password has been set from environment variable");
            log.info("   ⚠️  Please change the password after first login!");
        } else {
            log.info("Admin user already exists. Skipping initialization.");
        }
    }
}
