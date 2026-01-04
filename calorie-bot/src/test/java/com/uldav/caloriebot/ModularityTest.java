package com.uldav.caloriebot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTest {

    @Test
    @DisplayName("Verifies modules are created correctly")
    void modularityTest() {
        ApplicationModules applicationModules = ApplicationModules.of(CalorieBotApplication.class);
        applicationModules.forEach(System.out::println);
        applicationModules.verify();
        new Documenter(applicationModules).writeDocumentation();
    }
}
