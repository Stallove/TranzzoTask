package org.tranzzo.task.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tranzzo.task.dto.CurrencyRateDto;
import org.tranzzo.task.services.CurrencyRateService;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyRateService currencyRateService;

    @GetMapping(value = "/currency-rates")
    public Mono<Map<String, List<CurrencyRateDto>>> getCurrencyRates() {
        return currencyRateService.getFetchedCurrencyRates();
    }
}
