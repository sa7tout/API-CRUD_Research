package com.hotel.test.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.visualizers.backend.BackendListenerClient;
import org.apache.jmeter.visualizers.backend.BackendListenerContext;
import io.micrometer.core.instrument.Timer;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ResultsCollector implements BackendListenerClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, List<TestResult>> results = new ConcurrentHashMap<>();
    private final Map<String, MetricAggregator> aggregators = new ConcurrentHashMap<>();
    private final PrometheusMeterRegistry meterRegistry;

    public ResultsCollector() {
        this.meterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    @Override
    public void setupTest(BackendListenerContext context) {
        // Initialize test setup if needed
    }

    @Override
    public void handleSampleResults(List<SampleResult> sampleResults, BackendListenerContext context) {
        sampleResults.forEach(this::processSampleResult);
    }

    public void handleSampleResult(BackendListenerContext context, SampleResult sampleResult) {
        processSampleResult(sampleResult);
    }

    @Override
    public void teardownTest(BackendListenerContext context) {
        generateReport();
    }

    @Override
    public Arguments getDefaultParameters() {
        return new Arguments();
    }

    private void processSampleResult(SampleResult result) {
        String api = result.getSampleLabel().split("-")[0];
        MetricAggregator aggregator = aggregators.computeIfAbsent(api, k -> new MetricAggregator());

        Timer.builder("api.request.latency")
                .tag("api", api)
                .register(meterRegistry)
                .record(result.getTime(), TimeUnit.MILLISECONDS);

        meterRegistry.gauge("api.throughput", Collections.singletonList(io.micrometer.core.instrument.Tag.of("api", api)),
                1.0 / (result.getTime() / 1000.0));

        meterRegistry.gauge("api.cpu.usage", Collections.singletonList(io.micrometer.core.instrument.Tag.of("api", api)),
                result.getLatency() / 1000.0);

        meterRegistry.gauge("api.memory.usage", Collections.singletonList(io.micrometer.core.instrument.Tag.of("api", api)),
                result.getBytes());

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

    private void generateReport() {
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