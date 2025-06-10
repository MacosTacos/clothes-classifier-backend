package org.example.clothesclassifier.dtos;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadDTO {
    private Long id;
    private MultipartFile file;

    public FileUploadDTO(Long id, MultipartFile file) {
        this.id = id;
        this.file = file;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
