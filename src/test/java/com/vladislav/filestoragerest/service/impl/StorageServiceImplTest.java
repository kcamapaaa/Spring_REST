package com.vladislav.filestoragerest.service.impl;

import com.vladislav.filestoragerest.AbstractTestClass;
import com.vladislav.filestoragerest.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test-containers-flyway")
@Transactional
public class StorageServiceImplTest extends AbstractTestClass {
    @Autowired
    private StorageServiceImpl storageService;
    private static MockMultipartFile mockMultipartFile;
    private static final String FILE_NAME = "hello.txt";

    @BeforeAll
    public static void setup() {
        mockMultipartFile = TestUtils.returnMockMultipartFile();
    }


    @Test
    public void uploadFile_shouldUploadFile_andReturnLinkBack() {
        String linkToFile = storageService.uploadFile(mockMultipartFile);

        assertThat(linkToFile).isEqualTo("https://vlademelin.s3.eu-west-2.amazonaws.com/" + FILE_NAME);
    }

    @Test
    public void deleteFile_shouldReturnFilename_whenSuccessfullyRemoved() {
        storageService.uploadFile(mockMultipartFile);

        String returnedFileName = storageService.deleteFile(FILE_NAME);

        assertThat(returnedFileName).isEqualTo(FILE_NAME + " removed");
    }
}
