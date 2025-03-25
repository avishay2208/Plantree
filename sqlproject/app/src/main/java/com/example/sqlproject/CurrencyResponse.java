package com.example.sqlproject;

import java.util.Map;

public class CurrencyResponse {
    private final Map<String, Double> conversion_rates;

    public CurrencyResponse(Map<String, Double> conversionRates) {
        conversion_rates = conversionRates;
    }

    public Map<String, Double> getConversionRates() {
        return conversion_rates;
    }
}