package org.tranzzo.task.suppliers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.tranzzo.task.dto.CurrencyRateDto;
import org.tranzzo.task.model.CurrencyRate;
import org.tranzzo.task.repos.CurrencyRateRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class SupplierImplTest {

    private static final String TYPE = "test";

    @Mock
    private CurrencyRateRepository repository;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestSpec;

    private SupplierImpl supplier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        supplier = new SupplierImpl(TYPE, repository, requestSpec);
    }

    @Test
    void testGetCurrencyRates_Success() {
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(requestSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(CurrencyRateDto.class))
                .thenReturn(Flux.just(
                        new CurrencyRateDto("USD", new BigDecimal("74.5")),
                        new CurrencyRateDto("EUR", new BigDecimal("88.3"))
                ));

        when(repository.findByCurrencyAndType(anyString(), anyString())).thenReturn(Mono.empty());
        when(repository.save(any(CurrencyRate.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(supplier.getCurrencyRates())
                .expectNextMatches(rates ->
                        rates.size() == 2 &&
                                rates.getFirst().getCurrency().equals("USD") &&
                                rates.getFirst().getRate().compareTo(new BigDecimal("74.5")) == 0 &&
                                rates.get(1).getCurrency().equals("EUR") &&
                                rates.get(1).getRate().compareTo(new BigDecimal("88.3")) == 0
                )
                .verifyComplete();

        verify(requestSpec).retrieve();
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToFlux(CurrencyRateDto.class);
        verify(repository, times(2)).save(any(CurrencyRate.class));
    }
}