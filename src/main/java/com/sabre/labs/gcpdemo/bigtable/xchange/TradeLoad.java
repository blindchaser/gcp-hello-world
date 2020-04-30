package com.sabre.labs.gcpdemo.bigtable.xchange;


import lombok.Builder;
import lombok.Data;
import org.knowm.xchange.dto.marketdata.Trade;

@Data
@Builder
public class TradeLoad {
    private Trade trade;
    private String exchange;
}
