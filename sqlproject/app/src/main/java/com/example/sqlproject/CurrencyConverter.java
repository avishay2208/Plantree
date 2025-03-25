package com.example.sqlproject;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
public class CurrencyConverter {

    private static final String API_KEY = "398203c70db63c0ae1767aba";
    private static final String URL_ILS = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/ILS";
    private static final OkHttpClient client = new OkHttpClient();

    public static double getUSD_Rate() throws IOException {
        double usdRate = 1;

        Request requestForILS = new Request.Builder()
                .url(URL_ILS)
                .build();

        try (Response response = client.newCall(requestForILS).execute()) {
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                Gson gson = new Gson();
                CurrencyResponse currencyResponse = gson.fromJson(responseData, CurrencyResponse.class);
                if (currencyResponse != null) {
                    usdRate = currencyResponse.getConversionRates().get("USD");
                }
            }
        } catch (IOException e) {
            System.err.println("IOException occurred: " + e.getMessage());
            throw e;
        }

        return usdRate;
    }
}