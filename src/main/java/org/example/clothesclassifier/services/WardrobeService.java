package org.example.clothesclassifier.services;

import org.example.clothesclassifier.dtos.ClothingDTO;
import org.example.clothesclassifier.dtos.WeatherData;
import org.example.clothesclassifier.entities.ClothingEntity;
import org.example.clothesclassifier.entities.UserEntity;
import org.example.clothesclassifier.repositories.ClothingRepository;
import org.example.clothesclassifier.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WardrobeService {
    private final ClothingRepository clothingRepository;
    private final UserRepository userRepository;
    private final ClothingClassifierService classifierService;
    private final StorageService storageService;

    public WardrobeService(ClothingRepository clothingRepository,
                           UserRepository userRepository,
                           ClothingClassifierService classifierService,
                           StorageService storageService) {
        this.clothingRepository = clothingRepository;
        this.userRepository = userRepository;
        this.classifierService = classifierService;
        this.storageService = storageService;
    }


    public ClothingEntity addClothingItem(MultipartFile file, String login) throws IOException {
        UserEntity persistentUser = userRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + login));

        String clothingType = classifierService.classifyImage(file.getInputStream());

        String originalName = Optional.ofNullable(file.getOriginalFilename()).orElse("clothing");
        String fileKey = persistentUser.getId() + "_" + System.currentTimeMillis() + "_" + originalName;

        String imageUrl = storageService.uploadFile(file, fileKey);

        ClothingEntity clothing = new ClothingEntity(clothingType, imageUrl, persistentUser);
        clothingRepository.save(clothing);

        return clothing;
    }

    public List<ClothingDTO> getUserWardrobe(UserEntity user) {
        return clothingRepository.findByUser_Id(user.getId())
                .stream()
                .map(item -> new ClothingDTO(item.getImageUrl(), item.getType()))
                .toList();
    }

    public List<ClothingDTO> recommendClothing(UserEntity user, WeatherData weather) {
        List<ClothingEntity> wardrobe = clothingRepository.findByUser_Id(user.getId());
        List<ClothingEntity> recommendations = new ArrayList<>();

        long temp = weather.getTemp();

        for (ClothingEntity item : wardrobe) {
            String type = item.getType().toLowerCase();

            if (temp < 5) {
                if (type.contains("winterjacket") || type.contains("boots") || type.contains("hats") || type.contains("pants")) {
                    recommendations.add(item);
                }
            } else if (temp < 15) {
                if ((type.contains("boots") || type.contains("pants") || type.contains("blouse"))) {
                    recommendations.add(item);
                }
            } else if (temp < 20) {
                if ((type.contains("pants") || type.contains("blouse") || type.contains("sneakers") || type.contains("dress"))) {
                    recommendations.add(item);
                }
            } else if (temp < 25) {
                if ((type.contains("tshirt") || type.contains("dress") || type.contains("skirt") || type.contains("sneakers") || type.contains("pants"))) {
                    recommendations.add(item);
                }
            } else {
                if ((type.contains("tshirt") || type.contains("shorts") || type.contains("skirt") || type.contains("dress") || type.contains("sneakers"))) {
                    recommendations.add(item);
                }
            }
        }
        return recommendations
                .stream()
                .map(item -> new ClothingDTO(item.getImageUrl(), item.getType()))
                .toList();
    }

}
