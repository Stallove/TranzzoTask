package org.tranzzo.task.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("currency_rates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRate {
    @Id
    Long id;
    String type;
    String currency;
    BigDecimal rate;
}
