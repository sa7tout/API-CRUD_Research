package com.hotel.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.hotel.common.model")
@EnableJpaRepositories("com.hotel.common.repository")
public class GraphQLApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphQLApiApplication.class, args);
    }
}