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
import com.travel.journal.util.TravelMemoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TravelMemoServiceTest {

    @Mock
    private TravelMemoRepository travelMemoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TravelMemoMapper travelMemoMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private TravelMemoService travelMemoService;

    private UserEntity testUser;
    private TravelMemoEntity testMemo;
    private TravelMemoDto testMemoDto;
    private LocationEntity testLocation;
    private TagEntity testTag;

    @BeforeEach
    void setUp() {
        // Set up security context
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        lenient().when(jwt.getSubject()).thenReturn("test@example.com");

        // Set up test entities
        testUser = new UserEntity();
        testUser.setEmail("test@example.com");
        testUser.setDisplayName("Test User");

        testLocation = new LocationEntity();
        testLocation.setId(1);
        testLocation.setPlaceName("Paris");
        testLocation.setCity("Paris");
        testLocation.setCountry("France");
        testLocation.setLatitude(48.8566);
        testLocation.setLongitude(2.3522);

        testTag = new TagEntity();
        testTag.setId(1);
        testTag.setTag("adventure");
        testTag.setDescription("🏔️");

        testMemo = new TravelMemoEntity();
        testMemo.setId(1);
        testMemo.setTitle("Paris Trip");
        testMemo.setContent("Amazing adventure in Paris");
        testMemo.setImageurl("https://example.com/image.jpg");
        testMemo.setRating(4.5);
        testMemo.setMoodIcon("😊");
        testMemo.setDate(LocalDateTime.now());
        testMemo.setUser(testUser);
        testMemo.setLocations(new HashSet<>(Set.of(testLocation)));
        testMemo.setTags(new HashSet<>(Set.of(testTag)));

        testMemoDto = new TravelMemoDto(
                1,
            "Paris Trip",
            "Amazing adventure in Paris",
            "https://example.com/image.jpg",
                new ArrayList<>(),  // locations
            "", // historicalWeather
            4.5, // rating
            "😊", // moodIcon
                new ArrayList<>(), // tags
            LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void resolveOrCreateLocation_WhenLocationExists_ShouldReturnExisting() {
        // Given
        when(locationRepository.findByPlaceNameIgnoreCase("Paris")).thenReturn(Optional.of(testLocation));

        // When
        LocationEntity result = travelMemoService.resolveOrCreateLocation(testLocation);

        // Then
        assertEquals(testLocation, result);
        verify(locationRepository).findByPlaceNameIgnoreCase("Paris");
        verify(locationRepository, never()).save(any());
    }

    @Test
    void resolveOrCreateLocation_WhenLocationNotExists_ShouldCreateNew() {
        // Given
        LocationEntity newLocation = new LocationEntity();
        newLocation.setPlaceName("Tokyo");
        
        when(locationRepository.findByPlaceNameIgnoreCase("Tokyo")).thenReturn(Optional.empty());
        when(locationRepository.save(newLocation)).thenReturn(newLocation);

        // When
        LocationEntity result = travelMemoService.resolveOrCreateLocation(newLocation);

        // Then
        assertEquals(newLocation, result);
        verify(locationRepository).findByPlaceNameIgnoreCase("Tokyo");
        verify(locationRepository).save(newLocation);
    }

    @Test
    void resolveOrCreateTag_WhenTagExists_ShouldReturnExisting() {
        // Given
        when(tagRepository.findByTagIgnoreCase("adventure")).thenReturn(Optional.of(testTag));

        // When
        TagEntity result = travelMemoService.resolveOrCreateTag(testTag);

        // Then
        assertEquals(testTag, result);
        verify(tagRepository).findByTagIgnoreCase("adventure");
        verify(tagRepository, never()).save(any());
    }

    @Test
    void resolveOrCreateTag_WhenTagNotExists_ShouldCreateNew() {
        // Given
        TagEntity newTag = new TagEntity();
        newTag.setTag("food");
        
        when(tagRepository.findByTagIgnoreCase("food")).thenReturn(Optional.empty());
        when(tagRepository.save(newTag)).thenReturn(newTag);

        // When
        TagEntity result = travelMemoService.resolveOrCreateTag(newTag);

        // Then
        assertEquals(newTag, result);
        verify(tagRepository).findByTagIgnoreCase("food");
        verify(tagRepository).save(newTag);
    }

    @Test
    void createNewMemo_ShouldCreateMemoSuccessfully() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(locationRepository.findByPlaceNameIgnoreCase(anyString())).thenReturn(Optional.of(testLocation));
        when(tagRepository.findByTagIgnoreCase(anyString())).thenReturn(Optional.of(testTag));
        when(travelMemoRepository.save(any(TravelMemoEntity.class))).thenReturn(testMemo);
        when(travelMemoMapper.toDto(testMemo)).thenReturn(testMemoDto);

        // When
        TravelMemoDto result = travelMemoService.createNewMemo(testMemo);

        // Then
        assertNotNull(result);
        assertEquals(testMemoDto.title(), result.title());
        verify(userRepository).findByEmail("test@example.com");
        verify(travelMemoRepository).save(any(TravelMemoEntity.class));
        verify(travelMemoMapper).toDto(testMemo);
    }

    @Test
    void createNewMemo_WhenUserNotFound_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> travelMemoService.createNewMemo(testMemo));
        verify(userRepository).findByEmail("test@example.com");
        verify(travelMemoRepository, never()).save(any());
    }

    @Test
    void getAllMemosByEmail_WithValidUser_ShouldReturnMemos() {
        // Given
        String email = "test@example.com";
        List<TravelMemoEntity> memos = Arrays.asList(testMemo);
        when(travelMemoRepository.findByUserEmailWithAll(email)).thenReturn(memos);
        when(travelMemoMapper.toDto(testMemo)).thenReturn(testMemoDto);

        // When
        List<TravelMemoDto> result = travelMemoService.getAllMemosByEmail(email);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMemoDto.title(), result.getFirst().title());
        verify(travelMemoRepository).findByUserEmailWithAll(email);
        verify(travelMemoMapper).toDto(testMemo);
    }

    @Test
    void getAllMemosByEmail_WithDifferentUser_ShouldThrowAccessDeniedException() {
        // Given
        String differentEmail = "other@example.com";

        // When & Then
        assertThrows(AccessDeniedException.class, () -> travelMemoService.getAllMemosByEmail(differentEmail));
        verify(travelMemoRepository, never()).findByUserEmailWithAll(anyString());
    }

    @Test
    void getMemoById_WhenExists_ShouldReturnMemo() {
        // Given
        when(travelMemoRepository.findById(1)).thenReturn(Optional.of(testMemo));

        // When
        TravelMemoEntity result = travelMemoService.getMemoById(1);

        // Then
        assertNotNull(result);
        assertEquals(testMemo.getTitle(), result.getTitle());
        verify(travelMemoRepository).findById(1);
    }

    @Test
    void getMemoById_WhenNotExists_ShouldReturnNull() {
        // Given
        when(travelMemoRepository.findById(999)).thenReturn(Optional.empty());

        // When
        TravelMemoEntity result = travelMemoService.getMemoById(999);

        // Then
        assertNull(result);
        verify(travelMemoRepository).findById(999);
    }

    @Test
    void updateMemo_WithValidUser_ShouldUpdateSuccessfully() {
        // Given
        int memoId = 1;
        TravelMemoEntity updatedMemo = new TravelMemoEntity();
        updatedMemo.setTitle("Updated Title");
        updatedMemo.setContent("Updated Content");
        updatedMemo.setRating(5.0);

        when(travelMemoRepository.findById(memoId)).thenReturn(Optional.of(testMemo));
        when(travelMemoRepository.save(any(TravelMemoEntity.class))).thenReturn(testMemo);
        when(travelMemoMapper.toDto(testMemo)).thenReturn(testMemoDto);

        // When
        TravelMemoDto result = travelMemoService.updateMemo(memoId, updatedMemo);

        // Then
        assertNotNull(result);
        verify(travelMemoRepository).findById(memoId);
        verify(travelMemoRepository).save(testMemo);
        verify(travelMemoMapper).toDto(testMemo);
    }

    @Test
    void updateMemo_WithInvalidUser_ShouldThrowAccessDeniedException() {
        // Given
        int memoId = 1;
        UserEntity differentUser = new UserEntity();
        differentUser.setEmail("other@example.com");
        testMemo.setUser(differentUser);

        TravelMemoEntity updatedMemo = new TravelMemoEntity();
        updatedMemo.setTitle("Updated Title");

        when(travelMemoRepository.findById(memoId)).thenReturn(Optional.of(testMemo));

        // When & Then
        assertThrows(AccessDeniedException.class, () -> travelMemoService.updateMemo(memoId, updatedMemo));
        verify(travelMemoRepository).findById(memoId);
        verify(travelMemoRepository, never()).save(any());
    }

    @Test
    void updateMemo_WhenMemoNotFound_ShouldThrowNoSuchElementException() {
        // Given
        int memoId = 999;
        TravelMemoEntity updatedMemo = new TravelMemoEntity();

        when(travelMemoRepository.findById(memoId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> travelMemoService.updateMemo(memoId, updatedMemo));
        verify(travelMemoRepository).findById(memoId);
        verify(travelMemoRepository, never()).save(any());
    }

    @Test
    void deleteMemo_WithValidUser_ShouldDeleteSuccessfully() {
        // Given
        int memoId = 1;
        when(travelMemoRepository.findById(memoId)).thenReturn(Optional.of(testMemo));
        when(travelMemoRepository.existsById(memoId)).thenReturn(true);

        // When
        travelMemoService.deleteMemo(memoId);

        // Then
        verify(travelMemoRepository).findById(memoId);
        verify(travelMemoRepository).existsById(memoId);
        verify(travelMemoRepository).deleteById(memoId);
    }

    @Test
    void deleteMemo_WithInvalidUser_ShouldThrowAccessDeniedException() {
        // Given
        int memoId = 1;
        UserEntity differentUser = new UserEntity();
        differentUser.setEmail("other@example.com");
        testMemo.setUser(differentUser);

        when(travelMemoRepository.findById(memoId)).thenReturn(Optional.of(testMemo));

        // When & Then
        assertThrows(AccessDeniedException.class, () -> travelMemoService.deleteMemo(memoId));
        verify(travelMemoRepository).findById(memoId);
        verify(travelMemoRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteMemo_WhenMemoNotFound_ShouldThrowRuntimeException() {
        // Given
        int memoId = 999;
        when(travelMemoRepository.findById(memoId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> travelMemoService.deleteMemo(memoId));
        verify(travelMemoRepository).findById(memoId);
        verify(travelMemoRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteMemo_WhenMemoDoesNotExistInRepo_ShouldThrowRuntimeException() {
        // Given
        int memoId = 1;
        when(travelMemoRepository.findById(memoId)).thenReturn(Optional.of(testMemo));
        when(travelMemoRepository.existsById(memoId)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> travelMemoService.deleteMemo(memoId));
        verify(travelMemoRepository).findById(memoId);
        verify(travelMemoRepository).existsById(memoId);
        verify(travelMemoRepository, never()).deleteById(anyInt());
    }
}