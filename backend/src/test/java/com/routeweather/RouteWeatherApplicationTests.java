package com.routeweather;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "external.openrouteservice.api-key=stub")
class RouteWeatherApplicationTests {

    @Test
    void contextLoads() {
        // Verifies the Spring context starts without errors.
        // If this fails, check BeanConfiguration and adapter constructors.
    }
}
