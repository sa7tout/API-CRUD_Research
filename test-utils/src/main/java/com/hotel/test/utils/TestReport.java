package com.hotel.test.utils;

import lombok.Data;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TestReport {
    private Map<String, List<TestResult>> results;
    private Map<String, ApiSummary> summaries = new HashMap<>();

    public void calculateAverages() {
        results.forEach((api, testResults) -> {
            ApiSummary summary = new ApiSummary();
            summary.setAverageLatency(testResults.stream()
                    .mapToDouble(TestResult::getLatency)
                    .average()
                    .orElse(0.0));
            summary.setAverageThroughput(testResults.stream()
                    .mapToDouble(TestResult::getThroughput)
                    .average()
                    .orElse(0.0));
            summary.setAverageCpuUsage(testResults.stream()
                    .mapToDouble(TestResult::getCpuUsage)
                    .average()
                    .orElse(0.0));
            summary.setAverageMemoryUsage(testResults.stream()
                    .mapToDouble(TestResult::getMemoryUsage)
                    .average()
                    .orElse(0.0));
            summaries.put(api, summary);
        });
    }
}

@Data
class ApiSummary {
    private double averageLatency;
    private double averageThroughput;
    private double averageCpuUsage;
    private double averageMemoryUsage;
}