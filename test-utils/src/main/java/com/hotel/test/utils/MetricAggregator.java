package com.hotel.test.utils;

import java.util.LinkedList;
import java.util.Queue;

public class MetricAggregator {
    private final Queue<Double> latencies = new LinkedList<>();
    private final Queue<Double> throughputs = new LinkedList<>();
    private final Queue<Double> cpuUsages = new LinkedList<>();
    private final Queue<Double> memoryUsages = new LinkedList<>();
    private static final int WINDOW_SIZE = 100;

    public void addLatency(double value) {
        addMetric(latencies, value);
    }

    public void addThroughput(double value) {
        addMetric(throughputs, value);
    }

    public void addCpuUsage(double value) {
        addMetric(cpuUsages, value);
    }

    public void addMemoryUsage(double value) {
        addMetric(memoryUsages, value);
    }

    private void addMetric(Queue<Double> queue, double value) {
        queue.offer(value);
        if (queue.size() > WINDOW_SIZE) {
            queue.poll();
        }
    }

    public double getCurrentLatency() {
        return latencies.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    public double getCurrentThroughput() {
        return throughputs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    public double getCurrentCpuUsage() {
        return cpuUsages.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    public double getCurrentMemoryUsage() {
        return memoryUsages.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}