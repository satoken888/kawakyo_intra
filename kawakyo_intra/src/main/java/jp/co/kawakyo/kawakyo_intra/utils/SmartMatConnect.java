package jp.co.kawakyo.kawakyo_intra.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SmartMatConnect {
    
    public static int measure(String matId, String apiKey) {

        HttpURLConnection connection = null;
        int measuredWeight = 0;
        try{
            URL url = new URL("https://api.smartmat.io/v1/device/info?id=" + matId);
            connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("X-Smartmat-Key", apiKey);
            try (InputStream is = connection.getInputStream();
                     BufferedReader r = new BufferedReader(new InputStreamReader(is))) {
                    String res = r.lines().collect(Collectors.joining());
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode json = objectMapper.readTree(res);
                    
                    measuredWeight = json.get("deviceMeasurement").get("current").intValue();

                }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }

        return measuredWeight;
    }

}
