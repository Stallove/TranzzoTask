package org.tranzzo.task.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.tranzzo.task.dto.CurrencyRateDto;
import org.tranzzo.task.suppliers.Supplier;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CurrencyRateServiceTest {

    @Mock
    private Supplier supplier1;
    @Mock
    private Supplier supplier2;

    private CurrencyRateService currencyRateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyRateService = new CurrencyRateService(List.of(supplier1, supplier2));
    }

    @Test
    void testGetCurrencyRate() {
        when(supplier1.getType()).thenReturn("crypto");
        when(supplier2.getType()).thenReturn("fiat");

        List<CurrencyRateDto> rates1 = List.of(new CurrencyRateDto("BTC", new BigDecimal(123.33)));
        List<CurrencyRateDto> rates2 = List.of(new CurrencyRateDto("USD", new BigDecimal(123.33)));

        when(supplier1.getCurrencyRates()).thenReturn(Mono.just(rates1));
        when(supplier2.getCurrencyRates()).thenReturn(Mono.just(rates2));

        Mono<Map<String, List<CurrencyRateDto>>> result = currencyRateService.getFetchedCurrencyRates();

        verify(supplier1, times(1)).getCurrencyRates();
        verify(supplier2, times(1)).getCurrencyRates();
    }
}