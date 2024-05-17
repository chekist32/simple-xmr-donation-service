package com.sokol.simplemonerodonationservice.currencyconverter;

import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class CryptoCurrencyConverterImpl implements CryptoCurrencyConverter {
    private record ApiResponse(
            List<CryptoData> data,
            long timestamp
    ) { }

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

    private static final String url = "https://api.coincap.io/v2/assets?ids=monero";

    private final ConcurrentHashMap<String, Double> usdRates = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final RestTemplate restTemplate;


    public CryptoCurrencyConverterImpl() {
        restTemplate = new RestTemplate();
        executorService.scheduleAtFixedRate(
                this::refreshRates,
                0,
                30*1000L,
                TimeUnit.MILLISECONDS
        );
    }
    
    private void refreshRates() {
        try {
            ApiResponse response = restTemplate.getForObject(url, ApiResponse.class);

            if (response != null && response.data() != null)
                for (CryptoData data : response.data())
                    usdRates.put(data.symbol(), data.priceUsd());
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public double convertUsdToCrypto(double amount, CoinType coinType) {
        if (!usdRates.containsKey(coinType.name()))
            throw new RuntimeException("Currency conversion error"); // TODO

        return amount / usdRates.get(coinType.name());
    }

    public double convertCryptoToUsd(double amount, CoinType coinType) {
        if (!usdRates.containsKey(coinType.name()))
            throw new RuntimeException("Currency conversion error"); // TODO

        return amount * usdRates.get(coinType.name());
    }

}
