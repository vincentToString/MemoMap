package com.travel.journal.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Optional<String> getHistoricalWeatherCode(double lat, double lon, LocalDate date) {
        String url = String.format(
                "https://archive-api.open-meteo.com/v1/archive?latitude=%.4f&longitude=%.4f&start_date=%s&end_date=%s&daily=weathercode&timezone=auto",
                lat, lon, date, date
        );

        try {
            JsonNode root = restTemplate.getForObject(url, JsonNode.class);
            JsonNode daily = root.path("daily").path("weathercode");
            if (daily.isArray() && !daily.isEmpty()) {
                int code = daily.get(0).asInt();
                return Optional.of(mapWeatherCode(code));
            }
        } catch (Exception e) {
            // handle or log
        }

        return Optional.empty();
    }

    private String mapWeatherCode(int code) {
        if (code == 0) return "Clear";
        else if (code == 1) return "Mostly Clear";
        else if (code == 2) return "Partly Cloudy";
        else if (code == 3) return "Overcast";
        else if (code == 45 || code == 48) return "Fog";
        else if (code >= 51 && code <= 67) return "Drizzle";
        else if (code >= 71 && code <= 77) return "Snow";
        else if (code >= 80 && code <= 82) return "Rain Showers";
        else if (code == 95 || code == 96 || code == 99) return "Thunderstorm";
        else return "Unknown";
    }
}

