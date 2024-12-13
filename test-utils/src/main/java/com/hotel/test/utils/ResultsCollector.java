package com.hotel.test.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.visualizers.backend.AbstractBackendListenerClient;
import org.apache.jmeter.visualizers.backend.BackendListenerContext;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ResultsCollector extends AbstractBackendListenerClient {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, List<TestResult>> results = new ConcurrentHashMap<>();
    private final Map<String, MetricAggregator> aggregators = new ConcurrentHashMap<>();

    @Override
    public void handleSampleResults(List<SampleResult> sampleResults, BackendListenerContext context) {
        sampleResults.forEach(this::processSampleResult);
    }

    private void processSampleResult(SampleResult result) {
        String api = result.getSampleLabel().split("-")[0];
        MetricAggregator aggregator = aggregators.computeIfAbsent(api, k -> new MetricAggregator());

        aggregator.addLatency(result.getTime());
        aggregator.addThroughput(1.0 / (result.getTime() / 1000.0));
        aggregator.addCpuUsage(result.getLatency() / 1000.0);
        aggregator.addMemoryUsage(result.getBytes());

        TestResult testResult = new TestResult(
                api,
                Integer.parseInt(result.getThreadName().replaceAll("\\D", "")),
                result.getBodySize() < 5000 ? "small" : result.getBodySize() < 50000 ? "medium" : "large",
                result.getTime(),
                aggregator.getCurrentThroughput(),
                aggregator.getCurrentCpuUsage(),
                aggregator.getCurrentMemoryUsage(),
                LocalDateTime.now()
        );

        results.computeIfAbsent(api, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(testResult);
    }

    public void generateReport() {
        try {
            TestReport report = new TestReport();
            report.setResults(results);
            report.calculateAverages();
            objectMapper.writeValue(new File("test-results/performance-report.json"), report);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate report", e);
        }
    }
}

class MetricAggregator {
    private final Queue<Double> latencies = new LinkedList<>();
    private final Queue<Double> throughputs = new LinkedList<>();
    private final Queue<Double> cpuUsages = new LinkedList<>();
    private final Queue<Double> memoryUsages = new LinkedList<>();
    private static final int WINDOW_SIZE = 100;

    void addLatency(double value) {
        addMetric(latencies, value);
    }

    void addThroughput(double value) {
        addMetric(throughputs, value);
    }

    void addCpuUsage(double value) {
        addMetric(cpuUsages, value);
    }

    void addMemoryUsage(double value) {
        addMetric(memoryUsages, value);
    }

    private void addMetric(Queue<Double> queue, double value) {
        queue.offer(value);
        if (queue.size() > WINDOW_SIZE) {
            queue.poll();
        }
    }

    double getCurrentLatency() {
        return latencies.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    double getCurrentThroughput() {
        return throughputs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    double getCurrentCpuUsage() {
        return cpuUsages.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    double getCurrentMemoryUsage() {
        return memoryUsages.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}