package com.ttdashboard;

import java.util.Locale;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class
 */
@SpringBootApplication
public class TTdashboardApplication {

    private static final Set<String> VALID_DDL_AUTO_VALUES = Set.of(
            "none",
            "validate",
            "update",
            "create",
            "create-drop",
            "create-only",
            "drop");

    public static void main(String[] args) {
        sanitizeHibernateDdlAuto();
        SpringApplication.run(TTdashboardApplication.class, args);
    }

    private static void sanitizeHibernateDdlAuto() {
        String propertyKey = "spring.jpa.hibernate.ddl-auto";

        String ddlAutoValue = System.getProperty(propertyKey);
        if (ddlAutoValue == null || ddlAutoValue.isBlank()) {
            ddlAutoValue = System.getenv("SPRING_JPA_HIBERNATE_DDL_AUTO");
        }

        if (ddlAutoValue == null || ddlAutoValue.isBlank()) {
            return;
        }

        String normalizedValue = ddlAutoValue.trim().toLowerCase(Locale.ROOT);
        if (!VALID_DDL_AUTO_VALUES.contains(normalizedValue)) {
            // Prevent bad platform env var values from crashing Hibernate startup.
            System.setProperty(propertyKey, "update");
        }
    }

}