package com.travel.journal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.journal.config.SecurityConfig;
import com.travel.journal.dto.LocationRequest;
import com.travel.journal.dto.TravelMemoDto;
import com.travel.journal.dto.TravelMemoRequest;
import com.travel.journal.entity.TravelMemoEntity;
import com.travel.journal.service.EnrichService;
import com.travel.journal.service.TravelMemoService;
import com.travel.journal.util.TravelMemoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TravelMemoController.class)
class TravelMemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TravelMemoService travelMemoService;

    @MockitoBean
    private EnrichService enrichService;

    @MockitoBean
    private TravelMemoMapper travelMemoMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private TravelMemoDto testMemoDto;
    private TravelMemoEntity testMemoEntity;

    @BeforeEach
    void setUp() {
        testMemoEntity = new TravelMemoEntity();
        testMemoEntity.setId(1);
        testMemoEntity.setTitle("Test Trip");
        testMemoEntity.setContent("Amazing adventure in Paris");
        testMemoEntity.setImageurl("https://example.com/image.jpg");
        testMemoEntity.setRating(4.5);
        testMemoEntity.setMoodIcon("😊");
        testMemoEntity.setDate(LocalDateTime.now());

        // Create TravelMemoDto record with constructor
        testMemoDto = new TravelMemoDto(
                1,
            "Test Trip",
            "Amazing adventure in Paris", 
            "https://example.com/image.jpg",
            Collections.emptyList(), // locations
            "", // historicalWeather
            4.5, // rating
            "😊", // moodIcon
            Collections.emptyList(), // tags
            LocalDateTime.now(), // date
            LocalDateTime.now()
        );

        Jwt jwt = Jwt.withTokenValue("fake-token")
                     .header("alg", "none")
                     .claim("sub", "user@example.com")
                     .claim("scope", "read")
                     .build();

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void greeting_ShouldReturnHelloWorld() throws Exception {
        mockMvc.perform(get("/api/memos/greeting"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World"));
    }

//    @Test
//    @WithMockUser(username = "test@example.com")
//    void createMemo_ShouldReturnCreatedMemo() throws Exception {
//        // Given
//        when(travelMemoMapper.toEntity(any(TravelMemoDto.class))).thenReturn(testMemoEntity);
//        when(travelMemoService.createNewMemo(any(TravelMemoEntity.class))).thenReturn(testMemoDto);
//        TravelMemoRequest memoRequest = getTravelMemoRequest();
//        // When & Then
//        mockMvc.perform(post("/api/memos")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(memoRequest)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.title").value("Test Trip"))
//                .andExpect(jsonPath("$.content").value("Amazing adventure in Paris"))
//                .andExpect(jsonPath("$.rating").value(4.5))
//                .andExpect(jsonPath("$.moodIcon").value("😊"));
//
//        verify(travelMemoMapper).toEntity(any(TravelMemoDto.class));
//        verify(travelMemoService).createNewMemo(any(TravelMemoEntity.class));
//    }
//
//    private TravelMemoRequest getTravelMemoRequest() {
//        List<LocationRequest> locationRequests = List.of(
//                new LocationRequest("The Old Well", "Chapel Hill", "North Carolina", "USA")
//        );
//        return new TravelMemoRequest(
//                testMemoDto.id(),
//                testMemoDto.title(),
//                testMemoDto.content(),
//                testMemoDto.imageUrl(),
//                locationRequests,
//                testMemoDto.rating(),
//                testMemoDto.moodIcon(),
//                testMemoDto.tags(),
//                testMemoDto.date(),
//                testMemoDto.createdAt()
//        );
//    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getMemo_ShouldReturnMemosList() throws Exception {
        // Given
        String email = "test@example.com";
        TravelMemoDto memo1 = new TravelMemoDto(
                1,
            "Trip 1",
            "Content 1",
            "https://example.com/image1.jpg",
            Collections.emptyList(),
            "",
            3.0,
            "😊",
            Collections.emptyList(),
            LocalDateTime.now(),
                LocalDateTime.now()
        );
        
        TravelMemoDto memo2 = new TravelMemoDto(
                2,
            "Trip 2", 
            "Content 2",
            "https://example.com/image2.jpg",
            Collections.emptyList(),
            "",
            4.0,
            "😍", 
            Collections.emptyList(),
            LocalDateTime.now(),
                LocalDateTime.now()
        );
        
        List<TravelMemoDto> memosList = Arrays.asList(memo1, memo2);
        
        when(travelMemoService.getAllMemosByEmail(email)).thenReturn(memosList);

        // When & Then
        mockMvc.perform(get("/api/memos/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Trip 1"))
                .andExpect(jsonPath("$[1].title").value("Trip 2"));

        verify(travelMemoService).getAllMemosByEmail(email);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void updateMemo_ShouldReturnUpdatedMemo() throws Exception {
        // Given
        int memoId = 1;
        TravelMemoDto updatedDto = new TravelMemoDto(
                1,
            "Updated Trip",
            "Updated content",
            "https://example.com/updated.jpg",
            Collections.emptyList(),
            "",
            5.0,
            "🤩",
            Collections.emptyList(),
            LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(travelMemoMapper.toEntity(any(TravelMemoDto.class))).thenReturn(testMemoEntity);
        when(travelMemoService.updateMemo(eq(memoId), any(TravelMemoEntity.class))).thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/memos/{id}", memoId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Trip"))
                .andExpect(jsonPath("$.content").value("Updated content"))
                .andExpect(jsonPath("$.rating").value(5.0));

        verify(travelMemoMapper).toEntity(any(TravelMemoDto.class));
        verify(travelMemoService).updateMemo(eq(memoId), any(TravelMemoEntity.class));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void deleteMemo_ShouldReturnOkStatus() throws Exception {
        // Given
        int memoId = 1;
        doNothing().when(travelMemoService).deleteMemo(memoId);

        // When & Then
        mockMvc.perform(delete("/api/memos/{id}", memoId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(travelMemoService).deleteMemo(memoId);
    }

}