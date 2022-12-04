package com.vladislav.filestoragerest.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class StorageConfig {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String accessSecret;
    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonS3 generateS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);
        return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();
    }

//    @Bean
//    public DataSource getDataSource() {
//        return DataSourceBuilder.create()
//                .driverClassName("com.mysql.cj.jdbc.Driver")
//                .url("jdbc:mysql://localhost:3306/file_storage?serverTimezone=UTC")
//                .username("root")
//                .password("root")
//                .build();
//    }


}
