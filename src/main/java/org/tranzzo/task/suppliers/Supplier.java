package org.tranzzo.task.suppliers;

import org.tranzzo.task.dto.CurrencyRateDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface Supplier {
    String getType();
    Mono<List<CurrencyRateDto>> getCurrencyRates();
}
