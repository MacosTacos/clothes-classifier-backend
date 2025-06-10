package org.example.clothesclassifier.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;

@Service
public class StorageService {
    private final S3Client s3;

    public StorageService(@Value("${yc.access-key}") String accessKey, @Value("${yc.secret-key}") String secretKey) {
        this.s3 = S3Client.builder()
                .endpointOverride(URI.create("https://storage.yandexcloud.net"))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.EU_NORTH_1)
                .build();
    }

    public String uploadFile(MultipartFile file, String name) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket("ml-vkr")
                .key(name)
                .build();
        try {
            s3.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.format("https://storage.yandexcloud.net/%s/%s", "ml-vkr", name);
    }

    public boolean fileExists(String bucketName, String objectKey) {
        boolean flag = true;
        try {
            s3.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build());
        } catch (NoSuchKeyException e) {
            flag = false;
        }
        return flag;
    }
}


