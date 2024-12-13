package com.hotel.test.utils;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class PerformanceTestExecutor {
    private static final String JMETER_HOME = System.getProperty("jmeter.home", "test-utils/src/test/jmeter");
    private static final String[] TEST_TYPES = {"rest", "graphql", "soap", "grpc"};
    private static final int[] CONCURRENT_USERS = {10, 100, 500, 1000};
    private static final String[] PAYLOAD_SIZES = {"small", "medium", "large"};

    public static void runTests() throws Exception {
        initJMeter();

        for (String testType : TEST_TYPES) {
            for (int users : CONCURRENT_USERS) {
                for (String payloadSize : PAYLOAD_SIZES) {
                    runTest(testType, users, payloadSize);
                }
            }
        }
    }

    private static void initJMeter() {
        JMeterUtils.setJMeterHome(JMETER_HOME);
        JMeterUtils.loadJMeterProperties(Paths.get(JMETER_HOME, "jmeter.properties").toString());
        JMeterUtils.initLocale();
    }

    private static void runTest(String testType, int users, String payloadSize) throws Exception {
        String testPlan = Paths.get(JMETER_HOME, testType + "-test.jmx").toString();

        StandardJMeterEngine engine = new StandardJMeterEngine();
        HashTree testPlanTree = SaveService.loadTree(new File(testPlan));

        // Configure test parameters
        JMeterUtils.setProperty("threads", String.valueOf(users));
        JMeterUtils.setProperty("payload.size", payloadSize);

        engine.configure(testPlanTree);

        CompletableFuture.runAsync(() -> {
            engine.run();
            System.out.printf("Completed %s test with %d users and %s payload%n",
                    testType, users, payloadSize);
        });
    }
}