package com.s3Bucket.s3BucketApp.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(MultipartFile file);

    byte[] downloadFile(String fileName);

    String deleteFile(String fileName);
}
