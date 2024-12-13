package com.hotel.test.utils;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class PerformanceTestExecutor {
    private static final Logger LOGGER = Logger.getLogger(PerformanceTestExecutor.class.getName());
    private static final String[] TEST_TYPES = {"rest", "soap", "graphql", "grpc"};
    private static final int[] CONCURRENT_USERS = {10, 100, 500, 1000};

    public static void runTests() throws Exception {
        initJMeter();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String testType : TEST_TYPES) {
            for (int users : CONCURRENT_USERS) {
                futures.add(runTest(testType, users));
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private static void initJMeter() {
        String currentDir = System.getProperty("user.dir");
        File propsFile = new File(currentDir + "/test-utils/src/test/jmeter/jmeter.properties");

        if (!propsFile.exists()) {
            throw new RuntimeException("JMeter properties file not found: " + propsFile.getAbsolutePath());
        }

        JMeterUtils.setJMeterHome(propsFile.getParent());
        JMeterUtils.loadJMeterProperties(propsFile.getAbsolutePath());
        JMeterUtils.initLocale();
    }

    private static CompletableFuture<Void> runTest(String testType, int users) throws Exception {
        String currentDir = System.getProperty("user.dir");
        String testPlan = currentDir + "/test-utils/src/test/jmeter/" + testType + "-test-plan.jmx";
        File testPlanFile = new File(testPlan);

        if (!testPlanFile.exists()) {
            throw new RuntimeException("Test plan file not found: " + testPlan);
        }

        StandardJMeterEngine engine = new StandardJMeterEngine();
        HashTree testPlanTree = SaveService.loadTree(testPlanFile);

        engine.configure(testPlanTree);

        return CompletableFuture.runAsync(() -> {
            try {
                engine.run();
                LOGGER.info(() -> String.format("Completed %s test with %d concurrent users", testType, users));
            } catch (Exception e) {
                LOGGER.severe(() -> String.format("Test execution failed for %s with %d users: %s",
                        testType, users, e.getMessage()));
            }
        });
    }
}