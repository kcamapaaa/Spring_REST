package com.vladislav.filestoragerest.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.vladislav.filestoragerest.service.EventService;
import com.vladislav.filestoragerest.service.FileService;
import com.vladislav.filestoragerest.service.StorageService;
import com.vladislav.filestoragerest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

@Service
@Transactional
public class StorageServiceImpl implements StorageService {
    @Value("${application.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;

    @Autowired
    public StorageServiceImpl(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Transactional
    public String uploadFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        File fileObject = convertMultiPartFileToFile(file);
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObject));
        String linkToFile = s3Client.getUrl(bucketName, fileName).toString();
        fileObject.delete();
        return linkToFile;
    }

//    public ByteArrayResource downloadFile(String fileName) {
//        try {
//            S3Object s3Object = s3Client.getObject(bucketName, fileName);
//            S3ObjectInputStream inputStream = s3Object.getObjectContent();
//            byte[] bytes = IOUtils.toByteArray(inputStream);
//            ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
//            return byteArrayResource;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
        return fileName + " removed";
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertedFile;
    }
}
