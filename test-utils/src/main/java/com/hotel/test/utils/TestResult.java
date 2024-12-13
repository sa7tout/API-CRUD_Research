package com.hotel.test.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResult {
    private String api;
    private int concurrentUsers;
    private String payloadSize;
    private double latency;
    private double throughput;
    private double cpuUsage;
    private double memoryUsage;
    private LocalDateTime timestamp;
}