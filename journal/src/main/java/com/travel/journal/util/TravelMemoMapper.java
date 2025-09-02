package com.travel.journal.util;

import com.travel.journal.dto.LocationDto;
import com.travel.journal.dto.TagDto;
import com.travel.journal.dto.TravelMemoDto;
import com.travel.journal.entity.LocationEntity;
import com.travel.journal.entity.TagEntity;
import com.travel.journal.entity.TravelMemoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TravelMemoMapper {
    public TravelMemoDto toDto(TravelMemoEntity entity) {
        return new TravelMemoDto(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getImageurl(),
                entity.getLocations() != null ?
                        entity.getLocations().stream().map(this::toDto).toList() : List.of(),
                entity.getHistoricalWeather(),
                entity.getRating(),
                entity.getMoodIcon(),
                entity.getTags() != null ?
                        entity.getTags().stream().map(this::toDto).toList() : List.of(),
                entity.getDate(),
                entity.getCreatedAt()
        );
    }

    public TravelMemoEntity toEntity(TravelMemoDto dto) {
        TravelMemoEntity entity = new TravelMemoEntity();
        entity.setId(dto.id() != null ? dto.id() : 0);
        entity.setTitle(dto.title());
        entity.setContent(dto.content());
        entity.setImageurl(dto.imageUrl());
        entity.setHistoricalWeather(dto.historicalWeather());
        entity.setRating(dto.rating());
        entity.setMoodIcon(dto.moodIcon());
        entity.setDate(dto.date());

        if (dto.locations() != null) {
            entity.setLocations(dto.locations().stream()
                                   .map(this::toEntity)
                                   .collect(Collectors.toSet()));
        }

        if (dto.tags() != null) {
            entity.setTags(dto.tags().stream()
                              .map(this::toEntity)
                              .collect(Collectors.toSet()));
        }

        return entity;
    }


    private TagDto toDto(TagEntity tag) {
        return new TagDto(tag.getTag(), tag.getDescription());
    }

    private TagEntity toEntity(TagDto dto) {
        TagEntity tag = new TagEntity();
        tag.setTag(dto.tag());
        tag.setDescription(dto.description());
        return tag;
    }

    private LocationDto toDto(LocationEntity loc) {
        return new LocationDto(
                loc.getPlaceName(),
                loc.getCity(),
                loc.getRegion(),
                loc.getCountry(),
                loc.getLatitude(),
                loc.getLongitude()
        );
    }

    private LocationEntity toEntity(LocationDto dto) {
        LocationEntity loc = new LocationEntity();
        loc.setPlaceName(dto.placeName());
        loc.setCity(dto.city());
        loc.setRegion(dto.region());
        loc.setCountry(dto.country());
        loc.setLatitude(dto.latitude());
        loc.setLongitude(dto.longitude());
        return loc;
    }
}
