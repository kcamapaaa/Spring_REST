package com.vladislav.filestoragerest;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.security.Principal;

public final class TestUtils {
    private TestUtils() {
    }

    public static Principal getPrincipalForTest() {
        return () -> "user";
    }

    public static MockMultipartFile returnMockMultipartFile() {
        return new MockMultipartFile("file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
    }
}
