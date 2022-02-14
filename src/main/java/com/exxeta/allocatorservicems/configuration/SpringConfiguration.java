package com.exxeta.allocatorservicems.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {"com.exxeta.allocatorservice"})
@EnableJpaRepositories(basePackages = {"com.exxeta.allocatorservice"})
public class SpringConfiguration {

}
