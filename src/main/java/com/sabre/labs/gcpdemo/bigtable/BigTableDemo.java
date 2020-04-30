package com.sabre.labs.gcpdemo.bigtable;

import com.google.cloud.bigtable.beam.CloudBigtableIO;
import com.google.cloud.bigtable.beam.CloudBigtableScanConfiguration;
import com.sabre.labs.gcpdemo.bigtable.xchange.CryptoMarketTradeUnboundedSource;
import com.sabre.labs.gcpdemo.bigtable.xchange.ExchangeConfiguration;
import com.sabre.labs.gcpdemo.bigtable.xchange.TradeLoad;
import info.bitrich.xchangestream.bitstamp.v2.BitstampStreamingExchange;
import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.joda.time.Instant;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

// Some code are borrowed form https://github.com/galic1987/professional-services/tree/master/examples/cryptorealtime

@Component
public class BigTableDemo {
    private final static String projectID = "qwiklabs-gcp-01-295376d7324a";
    private final static String instanceId = "qwiklabs-gcp-01-295376d7324a";
    private final static String tableId = "market_data";

    private final static byte[] FAMILY = Bytes.toBytes("market");
    private final List<ExchangeConfiguration> allExchanges = new LinkedList<>();

    public BigTableDemo() {
        setupTradingPair();
    }

    private void setupTradingPair() {
        List<CurrencyPair> bitStampPair = new LinkedList<>();
        bitStampPair.add(CurrencyPair.BTC_USD);
        bitStampPair.add(CurrencyPair.ETH_USD);
        allExchanges.add(ExchangeConfiguration.builder()
                .exchange(BitstampStreamingExchange.class.getName())
                .exchangeKeyName("bitStamp")
                .listOfPairs(bitStampPair)
                .build());
    }

    static final DoFn<TradeLoad, Mutation> MUTATION_TRANSFORM = new DoFn<TradeLoad, Mutation>() {
        @ProcessElement
        public void processElement(DoFn<TradeLoad, Mutation>.ProcessContext c) {
            TradeLoad tl = c.element();
            Trade t = tl.getTrade();

            String rowkey = t.getCurrencyPair().toString() + "#" + tl.getExchange() + "#" + System.currentTimeMillis() + "#" + System.nanoTime();
            long delta = System.currentTimeMillis() - t.getTimestamp().getTime();

            c.outputWithTimestamp(
                    new Put(Bytes.toBytes(rowkey))
                            .addColumn(FAMILY, Bytes.toBytes("volume"), Bytes.toBytes(t.getOriginalAmount().toString()))
                            .addColumn(FAMILY, Bytes.toBytes("price"), Bytes.toBytes(t.getPrice().toString()))
                            .addColumn(FAMILY, Bytes.toBytes("orderType"), Bytes.toBytes(t.getType().toString()))
                            .addColumn(FAMILY, Bytes.toBytes("market"), Bytes.toBytes(tl.getExchange()))
                            .addColumn(FAMILY, Bytes.toBytes("delta"), Bytes.toBytes(Long.toString(delta)))
                            .addColumn(FAMILY, Bytes.toBytes("exchangeTime"), Bytes.toBytes(Long.toString(t.getTimestamp().getTime()))),
                    Instant.now()
            );
        }
    };

    public void run() {
        CloudBigtableCustomOptions options = PipelineOptionsFactory.as(CloudBigtableCustomOptions.class);
        options.setBigtableProjectId(projectID);
        options.setBigtableInstanceId(instanceId);
        options.setBigtableTableId(tableId);
        CloudBigtableScanConfiguration config = new CloudBigtableScanConfiguration.Builder()
                .withProjectId(options.getBigtableProjectId())
                .withInstanceId(options.getBigtableInstanceId())
                .withTableId(options.getBigtableTableId())
                .build();
        options.setStreaming(true);
        options.setRunner(DataflowRunner.class);
        options.setUsePublicIps(true);

        Pipeline p = Pipeline.create(options);

        allExchanges.forEach(exConf -> {
            exConf.getListOfPairs().forEach(cuPair -> {
                PCollection<TradeLoad> ptemp = p.apply(exConf.getExchangeKeyName(),
                        Read.from(new CryptoMarketTradeUnboundedSource(exConf.getExchange(), exConf.getExchangeKeyName(), cuPair)));
                ptemp
                        .apply(cuPair.base.toString() + "-" + cuPair.counter.toString() + " Mut", ParDo.of(MUTATION_TRANSFORM))
                        .apply(cuPair.base.toString() + "-" + cuPair.counter.toString(), CloudBigtableIO.writeToTable(config));
            });
        });

        p.run();
    }


}
