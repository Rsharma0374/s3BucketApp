package com.s3Bucket.s3BucketApp.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.s3Bucket.s3BucketApp.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 amazonS3;


    //Upload file
    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            File fileObj = convertMultipartToFile(file);
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
            fileObj.delete();
            return "File upload successful " + fileName;

        } catch (Exception e) {
            log.error("Exception occurred while uploading file to s3 bucket with probable cause -", e);
            return "File uploaded failed for file " + file.getOriginalFilename();
        }
    }

    private File convertMultipartToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)){
            fos.write(file.getBytes());
            fos.close();
            fos.flush();
        } catch (IOException e) {
            log.error("Exception occurred while converting file ", e);
        }
        return convertedFile;
    }

    //Download file
    public byte[] downloadFile(String fileName) {
        S3Object s3Object = amazonS3.getObject(bucketName, fileName);

        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;

        } catch (Exception e) {
            log.error("Exception occurred wile download file from s3 with probable cause - ", e);
            return null;
        }
    }

    //Delete File
    public String deleteFile(String fileName) {

        amazonS3.deleteObject(bucketName, fileName);
        return "File is deleted";
    }

    @Override
    public String getPreSignedUrl(String fileName) {
        try {
            String bucketName = "mys3bucketjava";
            String objectKey = "HDFC_CD_REGRESSION_27Mar_24.apk";
            Region region = Region.AP_SOUTH_1; // Specify your region

            // Create S3 Presigner using the default credential provider chain
            S3Presigner presigner = S3Presigner.builder()
                    .region(region)
                    .build();

            // Create GetObjectRequest
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            // Create GetObjectPresignRequest
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10)) // Set the duration for the pre-signed URL
                    .getObjectRequest(getObjectRequest)
                    .build();

            // Generate the pre-signed URL
            URL presignedUrl = presigner.presignGetObject(presignRequest).url();

            // Print the pre-signed URL
            System.out.println("Pre-Signed URL: " + presignedUrl);

            // Close the presigner
            presigner.close();
            return presignedUrl.toString();
        } catch (Exception e) {
            log.error("Exception occurred while getting file with cause - ", e);
            return "exception occurred";
        }
    }
}
