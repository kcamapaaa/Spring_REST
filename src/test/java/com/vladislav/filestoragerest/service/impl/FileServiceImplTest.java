package com.vladislav.filestoragerest.service.impl;

import com.vladislav.filestoragerest.AbstractTestClass;
import com.vladislav.filestoragerest.TestUtils;
import com.vladislav.filestoragerest.model.File;
import com.vladislav.filestoragerest.service.FileService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test-containers-flyway")
@Transactional
public class FileServiceImplTest extends AbstractTestClass {
    private static final long NO_FILE_ID = 0;
    private static final long HELLO_ID = 1;
    private static final long CAT_ID = 2;
    private static final String ADD_FILES_TO_DB = "classpath:add-files.sql";

    private static MockMultipartFile mockMultipartFile;
    private static Principal principal;

    @Autowired
    private FileService fileService;

    @BeforeAll
    public static void setup() {
        principal = TestUtils.getPrincipalForTest();
        mockMultipartFile = TestUtils.returnMockMultipartFile();
    }

    @Test
    @Sql(ADD_FILES_TO_DB)
    public void getAll_shouldReturnAllFiles_ifScriptWorksFine() {
        List<File> allFiles = fileService.getAll();

        assertThat(allFiles.size()).isEqualTo(2);
        assertThat(allFiles.get(0).getFileName()).isEqualTo("hello.txt");
        assertThat(allFiles.get(0).getLocation()).isEqualTo("https://vlademelin.s3.eu-west-2.amazonaws.com/hello.txt");
    }

    @Test
    public void getAll_shouldReturnNull_whenNoFilesPresent() {
        List<File> allFiles = fileService.getAll();

        assertThat(allFiles).isNull();
    }

    @Test
    @Sql(ADD_FILES_TO_DB)
    public void getById_shouldReturnFile_whenIsPresent() {
        File fileById = fileService.getById(HELLO_ID);

        assertThat(fileById.getFileName()).isEqualTo("hello.txt");
        assertThat(fileById.getLocation()).isEqualTo("https://vlademelin.s3.eu-west-2.amazonaws.com/hello.txt");
    }

    @Test
    public void getById_shouldReturnNull_whenIsPresent() {
        File fileById = fileService.getById(NO_FILE_ID);

        assertThat(fileById).isNull();
    }

    @Test
    @Sql(ADD_FILES_TO_DB)
    public void getByFilename_shouldReturnFile_whenIsPresent() {
        File fileByFilename = fileService.getByFileName("cat.jpg");

        assertThat(fileByFilename.getId()).isEqualTo(CAT_ID);
        assertThat(fileByFilename.getFileName()).isEqualTo("cat.jpg");
        assertThat(fileByFilename.getLocation()).isEqualTo("https://vlademelin.s3.eu-west-2.amazonaws.com/cat.jpg");
    }

    @Test
    public void getByFilename_shouldReturnNull_whenNoFile() {
        File fileByFilename = fileService.getByFileName("hello.txt");

        assertThat(fileByFilename).isNull();
    }

    @Test
    public void save_shouldThrowRuntimeException_whenUserIsUnknown() {
        Principal unknownUser = () -> "alex";
        assertThatThrownBy(() -> fileService.save(mockMultipartFile, unknownUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unknown user");
    }

    @Test
    public void save_shouldReturnSavedFile_whenFileCorrectlySaved() {
        File savedFile = fileService.save(mockMultipartFile, principal);

        assertThat(savedFile.getFileName()).isEqualTo("hello.txt");
    }

    @Test
    public void delete_shouldReturnFalse_ifFileNotFound() {
        boolean deleted = fileService.delete(NO_FILE_ID, principal);

        assertThat(deleted).isFalse();
    }

    @Test
    @Sql(ADD_FILES_TO_DB)
    public void delete_shouldReturnTrue_ifFileFound() {
        boolean deleted = fileService.delete(HELLO_ID, principal);
        File fileById = fileService.getById(HELLO_ID);


        assertThat(deleted).isTrue();
        assertThat(fileById.getStatus().name()).isEqualTo("DELETED");

    }
}
