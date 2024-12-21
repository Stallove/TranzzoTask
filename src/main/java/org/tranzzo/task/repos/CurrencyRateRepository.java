package org.tranzzo.task.repos;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import org.tranzzo.task.model.CurrencyRate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CurrencyRateRepository extends R2dbcRepository<CurrencyRate, Long> {
    Mono<CurrencyRate> findByCurrencyAndType(String currency, String type);
    Flux<CurrencyRate> findByType(String type);
}

