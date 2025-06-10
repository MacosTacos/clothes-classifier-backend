package org.example.clothesclassifier.controllers;

import org.example.clothesclassifier.dtos.ClothingDTO;
import org.example.clothesclassifier.dtos.WeatherData;
import org.example.clothesclassifier.entities.ClothingEntity;
import org.example.clothesclassifier.entities.UserEntity;
import org.example.clothesclassifier.services.WardrobeService;
import org.example.clothesclassifier.services.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/wardrobe")
public class WardrobeController {
    private final WardrobeService wardrobeService;
    private final WeatherService weatherService;

    public WardrobeController(WardrobeService wardrobeService, WeatherService weatherService) {
        this.wardrobeService = wardrobeService;
        this.weatherService = weatherService;
    }

    @GetMapping("/test")
    public void test(Principal principal) {
        System.out.println(principal.getName());
    }

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<ClothingDTO> uploadClothing(@RequestParam("file") MultipartFile file,
                                                      Principal principal) throws IOException {
        System.out.println(principal.getName());
        ClothingEntity saved = wardrobeService.addClothingItem(file, principal.getName());
        ClothingDTO resultDto = new ClothingDTO(saved.getImageUrl(), saved.getType());
        return ResponseEntity.ok(resultDto);
    }

    @GetMapping
    public ResponseEntity<List<ClothingDTO>> getWardrobe(@AuthenticationPrincipal UserEntity user) {
        List<ClothingEntity> items = wardrobeService.getUserWardrobe(user);
        List<ClothingDTO> itemDtos = items.stream()
                .map(item -> new ClothingDTO(item.getImageUrl(), item.getType()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDtos);
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<ClothingDTO>> recommendClothing(@RequestParam("lat") double latitude,
                                                               @RequestParam("lon") double longitude,
                                                               @AuthenticationPrincipal UserEntity user) {
        WeatherData weather = weatherService.getWeatherForLocation(latitude, longitude);
        List<ClothingEntity> recommendedItems = wardrobeService.recommendClothing(user, weather);
        List<ClothingDTO> resultDtos = recommendedItems.stream()
                .map(item -> new ClothingDTO(item.getImageUrl(), item.getType()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultDtos);
    }
}
