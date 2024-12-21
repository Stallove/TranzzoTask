package org.tranzzo.task.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tranzzo.task.dto.CurrencyRateDto;
import org.tranzzo.task.suppliers.Supplier;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CurrencyRateService {
    private final List<Supplier> suppliers;

    public Mono<Map<String, List<CurrencyRateDto>>> getFetchedCurrencyRates() {
        List<Mono<Map.Entry<String, List<CurrencyRateDto>>>> monoList = suppliers.stream()
                .map(supplier -> supplier.getCurrencyRates()
                        .map(rates -> Map.entry(supplier.getType(), rates)) // Map supplier type to rates
                )
                .toList();

        return Mono.zip(
                monoList, results -> Stream.of(results)
                            .map(result -> (Map.Entry<String, List<CurrencyRateDto>>) result)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))

        );
    }
}
