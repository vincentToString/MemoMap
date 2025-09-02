package com.travel.journal.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TravelMemoDto(
        Integer id,
        String title,
        String content,
        String imageUrl,
        List<LocationDto> locations,
        String historicalWeather,
        Double rating,
        String moodIcon,
        List<TagDto> tags,
        LocalDateTime date,
        LocalDateTime createdAt
) {}

