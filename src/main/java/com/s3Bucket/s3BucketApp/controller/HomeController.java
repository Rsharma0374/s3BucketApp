package com.s3Bucket.s3BucketApp.controller;

import com.s3Bucket.s3BucketApp.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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


    @GetMapping("/downloadFileWithIamRole/{fileName}")
    public String downloadFileWithIamRole(@PathVariable String fileName) {
        String data = storageService.downloadFileWithIamRole(fileName);

        return data;
    }

}
