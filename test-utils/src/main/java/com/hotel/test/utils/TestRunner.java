package com.hotel.test.utils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.hotel")
public class TestRunner {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(TestRunner.class, args);

        // Ensure database is populated
        Thread.sleep(5000);

        // Run performance tests
        PerformanceTestExecutor.runTests();
    }
}