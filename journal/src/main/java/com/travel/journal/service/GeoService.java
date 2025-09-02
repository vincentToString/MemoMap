package com.travel.journal.service;

import com.travel.journal.config.SecurityConfig;
import com.travel.journal.dto.MapboxResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GeoService {
    @Value("${mapbox.token}")
    private String apiToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    public Optional<double[]> getLatLng(String place, String city, String state, String country) {
        String query = Stream.of(place, city, state, country)
                     .filter(s -> s != null && !s.isEmpty())
                     .collect(Collectors.joining(", "));

        String url = "https://api.mapbox.com/geocoding/v5/mapbox.places/" +
                UriUtils.encodeQuery(query, StandardCharsets.UTF_8) +
                ".json?access_token=" + apiToken;

        try {
            MapboxResponse response = restTemplate.getForObject(url, MapboxResponse.class);
            if (response != null && !response.getFeatures().isEmpty()) {
                List<Double> coords = response.getFeatures().getFirst().getGeometry().getCoordinates();
                return Optional.of(new double[]{coords.getFirst(), coords.get(1)});
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return Optional.empty();

    }
}
