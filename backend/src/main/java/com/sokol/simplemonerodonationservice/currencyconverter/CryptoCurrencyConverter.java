package com.sokol.simplemonerodonationservice.currencyconverter;

import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class CryptoCurrencyConverter {
    private record ApiResponse(
            List<CryptoData> data,
            long timestamp
    ) {

    }

    private record CryptoData(
            String id,
            int rank,
            String symbol,
            String name,
            double supply,
            double maxSupply,
            double marketCapUsd,
            double volumeUsd24Hr,
            double priceUsd,
            double changePercent24Hr,
            double vwap24Hr,
            String explorer
    ) {}

    private static final ConcurrentHashMap<String, Double> usdRates = new ConcurrentHashMap<>();
    private static final String url = "https://api.coincap.io/v2/assets?ids=monero";
    private final static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    static {
        executorService.scheduleAtFixedRate(
                CryptoCurrencyConverter::refreshRates,
                0,
                30*1000L,
                TimeUnit.MILLISECONDS
        );
    }

    private CryptoCurrencyConverter() {}
    
    private static void refreshRates() {
        RestTemplate restTemplate = new RestTemplate();

        try {
            ApiResponse response = restTemplate.getForObject(url, ApiResponse.class);

            if (response != null && response.data() != null)
                for (CryptoData data : response.data())
                    usdRates.put(data.symbol(), data.priceUsd());
        } catch (Exception ex) {ex.printStackTrace();}
    }

    public static double convertUsdToCrypto(double amount, CoinType coinType) {
        if (!usdRates.containsKey(coinType.name()))
            throw new RuntimeException("Currency conversion error"); // TODO

        return amount / usdRates.get(coinType.name());
    }

    public static double convertCryptoToUsd(double amount, CoinType coinType) {
        if (!usdRates.containsKey(coinType.name()))
            throw new RuntimeException("Currency conversion error"); // TODO

        return amount * usdRates.get(coinType.name());
    }

}
