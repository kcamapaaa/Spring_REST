package com.vladislav.filestoragerest;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

public class AbstractTestClass {
    private static MySQLContainer mySQLContainer = (MySQLContainer) new MySQLContainer("mysql:latest")
            .withReuse(true);

    @BeforeAll
    public static void setUp() {
        mySQLContainer.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }
}
