package com.hotel.test.utils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hotel.test.utils", "com.hotel.common"})
@EntityScan(basePackages = {"com.hotel.common.model"})
@EnableJpaRepositories(basePackages = {"com.hotel.common.repository"})
public class TestRunner {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(TestRunner.class, args);

        // Ensure database is populated
        Thread.sleep(5000);

        // Run performance tests
        PerformanceTestExecutor.runTests();
    }
}