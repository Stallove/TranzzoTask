package org.tranzzo.task.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRateDto {
    private String currency;
    private BigDecimal rate;

    @JsonCreator
    public CurrencyRateDto(
            @JsonProperty("currency") String currency,
            @JsonProperty("rate") BigDecimal rate,
            @JsonProperty("name") String name,
            @JsonProperty("value") BigDecimal value
    ) {
        this.currency = (currency != null) ? currency : name;
        this.rate = (rate != null) ? rate : value;
    }
}
