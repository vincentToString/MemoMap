package com.travel.journal.controller;


import com.travel.journal.dto.TravelMemoDto;
import com.travel.journal.dto.TravelMemoRequest;
import com.travel.journal.entity.TravelMemoEntity;
import com.travel.journal.service.EnrichService;
import com.travel.journal.service.TravelMemoService;
import com.travel.journal.util.TravelMemoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memos")
@Tag(name = "Travel Memos", description = "Operations related to travel journal memos")
@SecurityRequirement(name = "bearerAuth") // Enables JWT "Authorize" support
public class TravelMemoController {

    private final TravelMemoService travelMemoService;
    private final TravelMemoMapper travelMemoMapper;
    private final EnrichService enrichService;

    public TravelMemoController(TravelMemoMapper travelMemoMapper, TravelMemoService travelMemoService, EnrichService enrichService) {
        this.travelMemoMapper = travelMemoMapper;
        this.travelMemoService = travelMemoService;
        this.enrichService = enrichService;
    }

    @Operation(summary = "Health check / greeting endpoint")
    @GetMapping("/greeting")
    public ResponseEntity<String> greeting() {
        return ResponseEntity.ok("Hello World");
    }

    @Operation(summary = "Create a new travel memo")
    @PostMapping
    public ResponseEntity<TravelMemoDto> createMemo(@RequestBody TravelMemoRequest travelMemoRequest) {
        TravelMemoDto enrichedDto = enrichService.enrich(travelMemoRequest);
        TravelMemoDto newDto = travelMemoService.createNewMemo(travelMemoMapper.toEntity(enrichedDto));
        return new ResponseEntity<>(newDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all travel memos by user email")
    @GetMapping("/{email}")
    public ResponseEntity<List<TravelMemoDto>> getMemo(@PathVariable String email) {
        List<TravelMemoDto> memos = travelMemoService.getAllMemosByEmail(email);
        return ResponseEntity.ok(memos);
    }

    @Operation(summary = "Update an existing memo by ID")
    @PutMapping("/{id}")
    public ResponseEntity<TravelMemoDto> updateMemo(@PathVariable int id, @RequestBody TravelMemoDto travelMemoDto) {
        TravelMemoDto newDto = travelMemoService.updateMemo(id, travelMemoMapper.toEntity(travelMemoDto));
        return new ResponseEntity<>(newDto, HttpStatus.OK);
    }

    @Operation(summary = "Delete a memo by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMemo(@PathVariable Integer id) {
        travelMemoService.deleteMemo(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}