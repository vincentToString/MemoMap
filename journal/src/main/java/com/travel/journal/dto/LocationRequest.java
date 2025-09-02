package com.travel.journal.dto;

public record LocationRequest(
        String placeName,
        String city,
        String region,
        String country
){}
