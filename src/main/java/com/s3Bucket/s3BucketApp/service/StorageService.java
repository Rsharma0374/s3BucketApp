package com.s3Bucket.s3BucketApp.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface StorageService {
    String uploadFile(MultipartFile file);

    byte[] downloadFile(String fileName);

    String deleteFile(String fileName);

    String getPreSignedUrl(String fileName);

    String uploadApkS3(File uploadedFile, String uploadS3Request);
}
