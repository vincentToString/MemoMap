package com.travel.journal.dto;

public record LocationDto(
        String placeName,
        String city,
        String region,
        String country,
        double latitude,
        double longitude
) {}

