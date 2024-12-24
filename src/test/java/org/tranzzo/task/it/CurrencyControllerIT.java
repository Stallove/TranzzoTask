package org.tranzzo.task.it;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.tranzzo.task.model.CurrencyRate;
import org.tranzzo.task.repos.CurrencyRateRepository;

import java.math.BigDecimal;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CurrencyControllerIT {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureTestcontainers(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CurrencyRateRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll().block();
    }

    @Test
    void testGetCurrencyRates_whenNoResponseSupplier_provideDataFromDb() {
        repository.save(new CurrencyRate(null, "crypto", "BTC", BigDecimal.valueOf(30000))).block();
        repository.save(new CurrencyRate(null, "crypto", "ETH", BigDecimal.valueOf(2000))).block();

        webTestClient.get()
                .uri("/currency-rates")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.crypto[0].currency").isEqualTo("BTC")
                .jsonPath("$.crypto[0].rate").isEqualTo(30000)
                .jsonPath("$.crypto[1].currency").isEqualTo("ETH")
                .jsonPath("$.crypto[1].rate").isEqualTo(2000);
    }

    @Test
    void testGetCurrencyRates_whenNoResponseSupplierAndEmptyDb_provideEmptyListOfSuppliersType() {
        webTestClient.get()
                .uri("/currency-rates")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.fiat").isArray()
                .jsonPath("$.fiat").isEmpty()
                .jsonPath("$.crypto").isArray()
                .jsonPath("$.crypto").isEmpty();
    }
}

