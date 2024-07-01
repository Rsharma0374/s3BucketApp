package com.s3Bucket.s3BucketApp.controller;

import com.s3Bucket.s3BucketApp.service.StorageService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/s3bucket")
public class HomeController {

    @Autowired
    StorageService storageService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to s3bucket";
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFile(@RequestParam (value = "file") MultipartFile file) {
        return new ResponseEntity<>(storageService.uploadFile(file), HttpStatus.OK);
    }

    @GetMapping("/downloadFile/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        byte[] data = storageService.downloadFile(fileName);

        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }


    @GetMapping("/deleteFile/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        return new ResponseEntity<>(storageService.deleteFile(fileName), HttpStatus.OK);
    }


    @GetMapping("/get-pre-signed-url/{fileName}")
    public String getPreSignedUrl(@PathVariable String fileName) {
        String data = storageService.getPreSignedUrl(fileName);

        return data;
    }

    @PostMapping(value = "/upload-apk-s3")
    public ResponseEntity<String > uploadApkS3(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam String uploadS3Request ) {


        File uploadedFile = saveFileToStagingDirectory(file);
        String baseResponse= storageService.uploadApkS3(uploadedFile, uploadS3Request);

        FileUtils.deleteQuietly(uploadedFile);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    public static File saveFileToStagingDirectory(MultipartFile file) {

        String newFilenameBase = UUID.randomUUID().toString();
        String fileUploadDirectory = "/tmp";

        String originalFileExtension = file.getOriginalFilename().substring(
                file.getOriginalFilename().lastIndexOf(".")
        );

        String newFilename = newFilenameBase + originalFileExtension;

        String storageDirectory = fileUploadDirectory;

        File newFile = new File(storageDirectory + "/" + newFilename);

        try {

            if(!newFile.exists())
            {
                File parentFile = newFile.getParentFile();

                if(!parentFile.isDirectory()) {
                    parentFile.mkdirs();
                }

            }

            file.transferTo(newFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFile;
    }

}
