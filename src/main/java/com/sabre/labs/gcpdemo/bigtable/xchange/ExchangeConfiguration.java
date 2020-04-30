package com.sabre.labs.gcpdemo.bigtable.xchange;

import lombok.Builder;
import lombok.Data;
import org.knowm.xchange.currency.CurrencyPair;

import java.util.List;

@Data
@Builder
public class ExchangeConfiguration {
    private String exchange;
    private String exchangeKeyName;
    private List<CurrencyPair> listOfPairs;
}
