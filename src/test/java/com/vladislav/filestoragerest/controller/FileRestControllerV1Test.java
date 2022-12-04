package com.vladislav.filestoragerest.controller;

import com.vladislav.filestoragerest.AbstractTestClass;
import com.vladislav.filestoragerest.TestUtils;
import com.vladislav.filestoragerest.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test-containers-flyway")
@AutoConfigureMockMvc
@Transactional
@Sql("classpath:add-files.sql")
class FileRestControllerV1Test extends AbstractTestClass {
    private static final String BASE_URL = "/api/v1/files/";
    @Autowired
    private MockMvc mvc;
    @Autowired
    FileService fileService;
    private static final String ALL_FILES_JSON = "[{\"id\":1,\"linkToFile\":\"https://vlademelin.s3.eu-west-2.amazonaws.com/hello.txt\",\"fileName\":\"hello.txt\",\"status\":\"ACTIVE\"}," +
            "{\"id\":2,\"linkToFile\":\"https://vlademelin.s3.eu-west-2.amazonaws.com/cat.jpg\",\"fileName\":\"cat.jpg\",\"status\":\"ACTIVE\"}]";

    @Test
    @WithMockUser(roles = "USER")
    public void getAllFiles_shouldReturnForbidden_whenGetFiles() throws Exception {
        mvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void getAllFiles_shouldReturnOK_whenGetFilesWithModerator() throws Exception {
        MvcResult mvcResult = mvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        assertThat(result).contains(ALL_FILES_JSON);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllFiles_shouldReturnOK_whenGetFilesWithAdmin() throws Exception {
        MvcResult mvcResult = mvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        assertThat(result).contains(ALL_FILES_JSON);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getFileById_shouldReturnForbidden_whenGetFileUser() throws Exception {
        mvc.perform(get(BASE_URL + "1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void getFileById_shouldReturnOK_whenGetFileWithModer() throws Exception {
        MvcResult mvcResult = mvc.perform(get(BASE_URL + "1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        assertThat(result).contains("{\"id\":1,\"linkToFile\":\"https://vlademelin.s3.eu-west-2.amazonaws.com/hello.txt\",\"fileName\":\"hello.txt\",\"status\":\"ACTIVE\"}");
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void getFileById_shouldReturnOK_whenGetFileWithAdmin() throws Exception {
        MvcResult mvcResult = mvc.perform(get(BASE_URL + "1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        assertThat(result).contains("{\"id\":1,\"linkToFile\":\"https://vlademelin.s3.eu-west-2.amazonaws.com/hello.txt\",\"fileName\":\"hello.txt\",\"status\":\"ACTIVE\"}");
    }

    @Test
    @WithMockUser(roles = "USER")
    public void uploadFile_shouldUploadFile_whenUser() throws Exception {
        MockMultipartFile mockMultipartFile = TestUtils.returnMockMultipartFile();

        mvc.perform(MockMvcRequestBuilders.multipart(BASE_URL)
                        .file(mockMultipartFile)
                        .principal(TestUtils.getPrincipalForTest()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void uploadFile_shouldUploadFile_whenModer() throws Exception {
        MockMultipartFile mockMultipartFile = TestUtils.returnMockMultipartFile();

        mvc.perform(MockMvcRequestBuilders.multipart(BASE_URL)
                        .file(mockMultipartFile)
                        .principal(TestUtils.getPrincipalForTest()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void uploadFile_shouldUploadFile_whenAdmin() throws Exception {
        MockMultipartFile mockMultipartFile = TestUtils.returnMockMultipartFile();

        mvc.perform(MockMvcRequestBuilders.multipart(BASE_URL)
                        .file(mockMultipartFile)
                        .principal(TestUtils.getPrincipalForTest()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteFileById_shouldReturnForbidden_whenUser() throws Exception {
        mvc.perform(delete(BASE_URL + "1").contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(TestUtils.getPrincipalForTest())))
                        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void deleteFileById_shouldReturnOk_whenModerator() throws Exception {
        String contentAsString = returnContentForDeleteModerAndAdmin();

        assertThat(contentAsString).contains("true");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteFileById_shouldReturnOk_whenAdmin() throws Exception {
        String contentAsString = returnContentForDeleteModerAndAdmin();

        assertThat(contentAsString).contains("true");
    }

    private String returnContentForDeleteModerAndAdmin() throws Exception {
        MvcResult mvcResult = mvc.perform(delete(BASE_URL + "1").contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(TestUtils.getPrincipalForTest())))
                        .andExpect(status().isOk())
                        .andReturn();

        return mvcResult.getResponse().getContentAsString();
    }

}