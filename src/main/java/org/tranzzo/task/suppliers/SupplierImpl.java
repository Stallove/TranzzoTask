package org.tranzzo.task.suppliers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.tranzzo.task.dto.CurrencyRateDto;
import org.tranzzo.task.exceptions.SupplierException;
import org.tranzzo.task.model.CurrencyRate;
import org.tranzzo.task.repos.CurrencyRateRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SupplierImpl implements Supplier {

    @Getter
    private final String type;
    private final CurrencyRateRepository repository;
    private final WebClient.RequestHeadersSpec<?> requestSpec;

    @Override
    public Mono<List<CurrencyRateDto>> getCurrencyRates() {
        return requestSpec
                .retrieve()
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new SupplierException("Supplier returned 500 status"))
                )
                .bodyToFlux(CurrencyRateDto.class)
                .collectList()
                .flatMap(this::saveUpdatateReacivedSupplierCurrencyRate)
                .onErrorResume(this::getLastSavedDataFromDb);
    }

    private Mono<List<CurrencyRateDto>> getLastSavedDataFromDb(Throwable error) {
        log.warn("Error retrieving data from supplier: {}", error.getMessage());
        log.info("Start taking last saved data from database.");
        return repository.findByType(type)
                .map(dbRate -> CurrencyRateDto.builder()
                        .currency(dbRate.getCurrency())
                        .rate(dbRate.getRate())
                        .build()
                )
                .collectList();
    }

    private Mono<List<CurrencyRateDto>> saveUpdatateReacivedSupplierCurrencyRate(List<CurrencyRateDto> currencyRateDtos) {
        log.info("Started saving or updating received data from supplier.");
        return saveCurrencyRates(Flux.fromIterable(currencyRateDtos), type)
                .map(savedRates -> savedRates.stream()
                        .map(savedRate -> CurrencyRateDto.builder()
                                .currency(savedRate.getCurrency())
                                .rate(savedRate.getRate())
                                .build())
                        .toList()
                );
    }

    private Mono<List<CurrencyRate>> saveCurrencyRates(Flux<CurrencyRateDto> supplierCryptoCurrencies, String type) {
        return supplierCryptoCurrencies
                .flatMap(dto -> repository.findByCurrencyAndType(dto.getCurrency(), type)
                        .flatMap(existing -> {
                            log.info("Updating {} {}: {} to database", type, existing.getCurrency(), existing.getRate());
                            existing.setRate(dto.getRate());
                            return repository.save(existing);
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            log.info("Saving {} {}: {} to database", type, dto.getCurrency(), dto.getRate());
                            CurrencyRate newCurrencyRate = CurrencyRate.builder()
                                    .type(type)
                                    .currency(dto.getCurrency())
                                    .rate(dto.getRate())
                                    .build();
                            return repository.save(newCurrencyRate);
                        }))
                )
                .collectList();
    }
}
