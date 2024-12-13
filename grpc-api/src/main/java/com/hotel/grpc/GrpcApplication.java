package com.hotel.grpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.hotel.common.model")
@EnableJpaRepositories("com.hotel.common.repository")
public class GrpcApplication {
    public static void main(String[] args) {
        SpringApplication.run(GrpcApplication.class, args);
    }
}