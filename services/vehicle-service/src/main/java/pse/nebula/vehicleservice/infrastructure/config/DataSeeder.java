package pse.nebula.vehicleservice.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.support.TransactionTemplate;
import pse.nebula.vehicleservice.domain.model.*;
import pse.nebula.vehicleservice.domain.port.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds the database with initial test data.
 * Only runs when the database is empty.
 * Disabled for test profile.
 */
@Configuration
@Profile("!test")
public class DataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedDatabase(
            VehicleRepository vehicleRepository,
            PaintRepository paintRepository,
            RimRepository rimRepository,
            InteriorRepository interiorRepository,
            TransactionTemplate transactionTemplate) {

        return args -> {
            // Only seed if database is empty (SQL init script may have already populated data)
            boolean hasVehicles = !vehicleRepository.findAll().isEmpty();
            boolean hasPaints = !paintRepository.findAll().isEmpty();

            if (hasVehicles && hasPaints) {
                logger.info("Database already contains data (from SQL init script), skipping Java seeder.");
                return;
            }

            if (!hasVehicles && !hasPaints) {
                logger.info("Seeding database with initial data...");

                // Wrap all seeding operations in a single transaction
                transactionTemplate.executeWithoutResult(status -> {
                    seedVehicles(vehicleRepository);
                    seedPaints(paintRepository);
                    seedRims(rimRepository);
                    seedInteriors(interiorRepository);
                });

                logger.info("Database seeding completed successfully!");
            } else {
                logger.warn("Database in inconsistent state - partial data exists. Manual intervention may be needed.");
            }
        };
    }

    private void seedVehicles(VehicleRepository repository) {
        List<Vehicle> vehicles = List.of(
                // SPORTS
                new Vehicle("Furari", CarType.SPORTS, 670, new BigDecimal("245000.00"), "furarri-hero", "/models/furarri.glb"),
                new Vehicle("GTR", CarType.SPORTS, 565, new BigDecimal("280000.00"), "gtr-hero", "/models/GTR.glb"),
                new Vehicle("Infernus", CarType.SPORTS, 580, new BigDecimal("320000.00"), "infernus-hero", "/models/Infernus.glb"),

                // SEDAN
                new Vehicle("Dacia", CarType.SEDAN, 150, new BigDecimal("22000.00"), "dacia-hero", "/models/Dacia.glb"),
                new Vehicle("Gauntlet", CarType.SEDAN, 425, new BigDecimal("75000.00"), "gauntlet-hero", "/models/Gauntlet.glb"),

                // SUV
                new Vehicle("Deviant", CarType.SUV, 380, new BigDecimal("85000.00"), "deviant-hero", "/models/Deviant.glb"),

                // LUXURY_COUPE
                new Vehicle("Deluxo", CarType.LUXURY_COUPE, 320, new BigDecimal("150000.00"), "deluxo-hero", "/models/Deluxo.glb"),
                new Vehicle("P-911", CarType.LUXURY_COUPE, 443, new BigDecimal("175000.00"), "p911-hero", "/models/P-911.glb"),
                new Vehicle("Roadstar", CarType.LUXURY_COUPE, 400, new BigDecimal("165000.00"), "roadstar-hero", "/models/Roadstar.glb"),

                // SUPERCAR
                new Vehicle("Impaler", CarType.SUPERCAR, 750, new BigDecimal("450000.00"), "impaler-hero", "/models/Impaler.glb"),
                new Vehicle("NFS Car", CarType.SUPERCAR, 820, new BigDecimal("520000.00"), "nfs-hero", "/models/NFS_car.glb"),
                new Vehicle("Tarzan Wonder", CarType.SUPERCAR, 900, new BigDecimal("680000.00"), "tarzan-hero", "/models/TarzanWonderCar.glb")
        );

        vehicles.forEach(repository::save);
        logger.info("Seeded {} vehicles", vehicles.size());
    }

    private void seedPaints(PaintRepository repository) {
        // Exact paint list matching frontend mock data
        List<PaintData> paintsData = List.of(
                new PaintData("Racing Red", "Classic racing red", "racing-red", "#DC2626", new BigDecimal("0.00")),
                new PaintData("Midnight Black", "Deep midnight black", "midnight-black", "#000000", new BigDecimal("0.00")),
                new PaintData("Pearl White", "Premium pearl white finish", "pearl-white", "#FFFFFF", new BigDecimal("1200.00")),
                new PaintData("Ocean Blue", "Vibrant ocean blue metallic", "ocean-blue", "#2563EB", new BigDecimal("1500.00")),
                new PaintData("Silver Metallic", "Elegant silver metallic", "silver-metallic", "#94A3B8", new BigDecimal("800.00")),
                new PaintData("Sunset Orange", "Bold sunset orange", "sunset-orange", "#F97316", new BigDecimal("1800.00")),
                new PaintData("Electric Green", "Vibrant electric green", "electric-green", "#10B981", new BigDecimal("2000.00"))
        );

        for (PaintData data : paintsData) {
            Paint paint = new Paint(data.name(), data.description(), data.visualKey(), data.hex());

            // Add same price for all car types (as per frontend mock data)
            paint.addPrice(new PaintPrice(CarType.SPORTS, data.price()));
            paint.addPrice(new PaintPrice(CarType.SEDAN, data.price()));
            paint.addPrice(new PaintPrice(CarType.SUV, data.price()));
            paint.addPrice(new PaintPrice(CarType.LUXURY_COUPE, data.price()));
            paint.addPrice(new PaintPrice(CarType.SUPERCAR, data.price()));

            repository.save(paint);
        }

        logger.info("Seeded {} paints with prices for all car types", paintsData.size());
    }

    private void seedRims(RimRepository repository) {
        // Exact rim list matching frontend mock data
        List<RimData> rimsData = List.of(
                new RimData("Sport 19\"", "Standard sport rims", "rim-sport", "sport", new BigDecimal("0.00")),
                new RimData("Performance 20\"", "High-performance lightweight rims", "rim-performance", "performance", new BigDecimal("2500.00")),
                new RimData("Premium 21\"", "Premium carbon fiber rims", "rim-premium", "premium", new BigDecimal("4800.00")),
                new RimData("Racing Pro", "Track-focused racing rims", "rim-racing-pro", "racing-pro", new BigDecimal("6500.00"))
        );

        for (RimData data : rimsData) {
            Rim rim = new Rim(data.name(), data.description(), data.image(), data.visualKey());

            // Add same price for all car types (as per frontend mock data)
            rim.addPrice(new RimPrice(CarType.SPORTS, data.price()));
            rim.addPrice(new RimPrice(CarType.SEDAN, data.price()));
            rim.addPrice(new RimPrice(CarType.SUV, data.price()));
            rim.addPrice(new RimPrice(CarType.LUXURY_COUPE, data.price()));
            rim.addPrice(new RimPrice(CarType.SUPERCAR, data.price()));

            repository.save(rim);
        }

        logger.info("Seeded {} rims with prices for all car types", rimsData.size());
    }

    private void seedInteriors(InteriorRepository repository) {
        // Exact interior list matching frontend mock data
        List<InteriorData> interiorsData = List.of(
                new InteriorData("Black Leather", "Premium black leather", "interior-black", "black", new BigDecimal("0.00")),
                new InteriorData("Beige Leather", "Luxury beige leather", "interior-beige", "beige", new BigDecimal("2000.00")),
                new InteriorData("Red Sport", "Sporty red leather with carbon accents", "interior-red", "red", new BigDecimal("2500.00")),
                new InteriorData("Carbon Fiber", "Carbon fiber racing seats", "interior-carbon", "carbon", new BigDecimal("4000.00"))
        );

        for (InteriorData data : interiorsData) {
            Interior interior = new Interior(data.name(), data.description(), data.image(), data.visualKey());

            // Add same price for all car types (as per frontend mock data)
            interior.addPrice(new InteriorPrice(CarType.SPORTS, data.price()));
            interior.addPrice(new InteriorPrice(CarType.SEDAN, data.price()));
            interior.addPrice(new InteriorPrice(CarType.SUV, data.price()));
            interior.addPrice(new InteriorPrice(CarType.LUXURY_COUPE, data.price()));
            interior.addPrice(new InteriorPrice(CarType.SUPERCAR, data.price()));

            repository.save(interior);
        }

        logger.info("Seeded {} interiors with prices for all car types", interiorsData.size());
    }

    // Helper records for seed data - matching exact frontend mock data
    private record PaintData(String name, String description, String visualKey, String hex, BigDecimal price) {
    }

    private record RimData(String name, String description, String image, String visualKey, BigDecimal price) {
    }

    private record InteriorData(String name, String description, String image, String visualKey, BigDecimal price) {
    }
}

