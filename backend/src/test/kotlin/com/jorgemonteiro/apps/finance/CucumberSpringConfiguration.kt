package com.jorgemonteiro.apps.finance

import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

/**
 * Cucumber Spring context configuration for integration tests.
 * Uses a shared Testcontainers PostgreSQL instance.
 */
@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
class CucumberSpringConfiguration {

    companion object {
        private val postgres = PostgreSQLContainer("postgres:17-alpine").apply {
            start()
        }

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
            registry.add("spring.docker.compose.enabled") { false }
        }
    }
}
