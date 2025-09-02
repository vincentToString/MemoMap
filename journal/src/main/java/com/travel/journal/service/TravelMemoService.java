package com.travel.journal.service;

import com.travel.journal.dto.TravelMemoDto;
import com.travel.journal.entity.LocationEntity;
import com.travel.journal.entity.TagEntity;
import com.travel.journal.entity.TravelMemoEntity;
import com.travel.journal.entity.UserEntity;
import com.travel.journal.repo.LocationRepository;
import com.travel.journal.repo.TagRepository;
import com.travel.journal.repo.TravelMemoRepository;
import com.travel.journal.repo.UserRepository;
import com.travel.journal.security.CustomOidcUser;
import com.travel.journal.util.TravelMemoMapper;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TravelMemoService {
    private final TravelMemoRepository travelMemoRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final TagRepository tagRepository;
    private final TravelMemoMapper travelMemoMapper;

    public TravelMemoService(TravelMemoRepository travelMemoRepository, UserRepository userRepository, LocationRepository locationRepository, TagRepository tagRepository, TravelMemoMapper travelMemoMapper) {
        this.travelMemoRepository = travelMemoRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.tagRepository = tagRepository;
        this.travelMemoMapper = travelMemoMapper;
    }
    public LocationEntity resolveOrCreateLocation(LocationEntity location) {
        Optional<LocationEntity> existing = locationRepository.findByPlaceNameIgnoreCase(location.getPlaceName());
        return existing.orElseGet(() -> locationRepository.save(location));
    }

    public TagEntity resolveOrCreateTag(TagEntity tag) {
        Optional<TagEntity> existing = tagRepository.findByTagIgnoreCase(tag.getTag());
        return existing.orElseGet(() -> tagRepository.save(tag));
    }

    // Create
    @Transactional
    @CacheEvict(value = "memos", allEntries = true)
    public TravelMemoDto createNewMemo(TravelMemoEntity travelMemoEntity) {
        Authentication auth = SecurityContextHolder.getContext()
                                                   .getAuthentication();
        String userEmail = extractFromAuth(auth);

        UserEntity author = userRepository.findByEmail(userEmail)
                                          .orElseThrow(() -> new RuntimeException("Author not found"));

        travelMemoEntity.setUser(author);

        Set<LocationEntity> resolved = new HashSet<>();
        for(LocationEntity location : travelMemoEntity.getLocations()) {
            resolved.add(resolveOrCreateLocation(location));
        }
        travelMemoEntity.setLocations(resolved);

        Set<TagEntity> resolvedTags = new HashSet<>();
        for(TagEntity tag : travelMemoEntity.getTags()) {
            resolvedTags.add(resolveOrCreateTag(tag));
        }
        travelMemoEntity.setTags(resolvedTags);

        travelMemoRepository.save(travelMemoEntity);
        return travelMemoMapper.toDto(travelMemoEntity);
    }

    // Read
    @Transactional(readOnly=true)
    @Cacheable(value="memos", key="#email")
    public List<TravelMemoDto> getAllMemosByEmail(String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = extractFromAuth(auth);

        if(!userEmail.equals(email)) {
            throw new AccessDeniedException("You do not have permission to view this list");
        }
        return travelMemoRepository.findByUserEmailWithAll(email).stream().map(travelMemoMapper::toDto).collect(Collectors.toCollection(ArrayList::new));
    }

    public TravelMemoEntity getMemoById(int id) {
        return travelMemoRepository.findById(id).orElse(null);
    }

    //update
    @Transactional
    @CacheEvict(value = {"memos"}, key = "#result.user.email")
    public TravelMemoDto updateMemo(int id, TravelMemoEntity updated) {
        TravelMemoEntity travelMemoEntity = travelMemoRepository.findById(id)
                                                                .orElseThrow(() -> new NoSuchElementException("Memo not found: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = extractFromAuth(auth);


        if (!userEmail.equals(travelMemoEntity.getUser().getEmail())) {
            throw new AccessDeniedException("You do not have permission to update this memo");
        }

        if (updated.getTitle() != null) travelMemoEntity.setTitle(updated.getTitle());
        if (updated.getContent() != null) travelMemoEntity.setContent(updated.getContent());
        if (updated.getImageurl() != null) travelMemoEntity.setImageurl(updated.getImageurl());
        if (updated.getHistoricalWeather() != null) travelMemoEntity.setHistoricalWeather(updated.getHistoricalWeather());
        if (updated.getRating() != null) travelMemoEntity.setRating(updated.getRating());
        if (updated.getMoodIcon() != null) travelMemoEntity.setMoodIcon(updated.getMoodIcon());
        if (updated.getDate() != null) travelMemoEntity.setDate(updated.getDate());

        travelMemoEntity.getTags().clear();
        if (updated.getTags() != null) {
            Set<TagEntity> resolvedTags = updated.getTags().stream()
                                                 .map(this::resolveOrCreateTag)
                                                 .collect(Collectors.toSet());
            travelMemoEntity.getTags().addAll(resolvedTags);
        }

        travelMemoEntity.getLocations().clear();
        if (updated.getLocations() != null) {
            Set<LocationEntity> resolvedLocations = updated.getLocations().stream()
                                                           .map(this::resolveOrCreateLocation)
                                                           .collect(Collectors.toSet());
            travelMemoEntity.getLocations().addAll(resolvedLocations);
        }

        travelMemoRepository.save(travelMemoEntity);
        return travelMemoMapper.toDto(travelMemoEntity);
    }


    @Transactional
    @CacheEvict(value={"memos"}, allEntries = true)
    public void deleteMemo(int id) {
        TravelMemoEntity travelMemoEntity = getMemoById(id);
        if (travelMemoEntity == null) {
            throw new RuntimeException("Memo not found with id: " + id);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = extractFromAuth(auth);
        if(!userEmail.equals(travelMemoEntity.getUser().getEmail())) {
            throw new AccessDeniedException("You do not have permission to update this memo");
        }
        if (!travelMemoRepository.existsById(id)) {
            throw new RuntimeException("Memo not found with id: " + id);
        }
        travelMemoRepository.deleteById(id);
    }

    private String extractFromAuth(Authentication auth) {
        if(auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }else {
            throw new IllegalStateException("Unexpected principal type: " + auth.getPrincipal().getClass());

        }
    }



}
