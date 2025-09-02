package com.travel.journal.service;

import com.travel.journal.dto.LocationDto;
import com.travel.journal.dto.LocationRequest;
import com.travel.journal.dto.TravelMemoDto;
import com.travel.journal.dto.TravelMemoRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnrichService {

    private final GeoService geoService;
    private final WeatherService weatherService;

    public EnrichService(GeoService geoService, WeatherService weatherService) {
        this.geoService = geoService;
        this.weatherService = weatherService;
    }

    public TravelMemoDto enrich(TravelMemoRequest travelMemoRequest) {
        List<LocationDto> enrichedLocations = new ArrayList<>();
        for(LocationRequest locReq: travelMemoRequest.locations()){

            geoService.getLatLng(locReq.placeName(), locReq.city(), locReq.region(), locReq.country()).ifPresent(coords -> {
                enrichedLocations.add(new LocationDto(
                        locReq.placeName(), locReq.city(), locReq.region(), locReq.country(), coords[0], coords[1]
                ));
            });
        }
        String weather = "N/A";
        LocationDto firstWithCoords = enrichedLocations.stream()
                                                       .findFirst()
                                                       .orElse(null);
        if (firstWithCoords != null) {
            weather = weatherService.getHistoricalWeatherCode(
                    firstWithCoords.latitude(),
                    firstWithCoords.longitude(),
                    travelMemoRequest.date().toLocalDate()
            ).orElse(null);
        }

        return new TravelMemoDto(
                null, // id will be set later
                travelMemoRequest.title(),
                travelMemoRequest.content(),
                travelMemoRequest.imageUrl(),
                enrichedLocations,
                weather,
                travelMemoRequest.rating(),
                travelMemoRequest.moodIcon(),
                travelMemoRequest.tags(),
                travelMemoRequest.date(),
                LocalDateTime.now() // createdAt

        );
    }
}
