package com.travel.journal.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TravelMemoRequest(
        Integer id,
        String title,
        String content,
        String imageUrl,
        List<LocationRequest> locations,
        Double rating,
        String moodIcon,
        List<TagDto> tags,
        LocalDateTime date,
        LocalDateTime createdAt
) {}
