package org.tranzzo.task.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.tranzzo.task.repos.CurrencyRateRepository;
import org.tranzzo.task.suppliers.Supplier;
import org.tranzzo.task.suppliers.SupplierImpl;

@Configuration
public class BaseConfigs {

    @Value("${currencies-provider.url}")
    private String currenciesProviderUrl;

    @Value("${currencies-provider.fiat-api-key}")
    private String apiKey;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(currenciesProviderUrl)
                .build();
    }

    @Bean
    public Supplier cryptoSupplier(CurrencyRateRepository repository, WebClient webClient) {
        return new SupplierImpl("crypto", repository, webClient.get().uri("/crypto-currency-rates"));
    }

    @Bean
    public Supplier fiatSupplier(CurrencyRateRepository repository, WebClient webClient) {
//        String apiKey = System.getenv("FIAT_API_KEY");
        return new SupplierImpl("fiat", repository, webClient.get().uri("/fiat-currency-rates")
                .header("x-api-key", apiKey));
    }
}
