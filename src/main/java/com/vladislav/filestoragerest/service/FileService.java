package com.vladislav.filestoragerest.service;

import com.vladislav.filestoragerest.model.File;
import com.vladislav.filestoragerest.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

public interface FileService extends GenericService<File, Long>{
    File save(MultipartFile file, Principal principal);
    File getByFileName(String fileName);

    boolean delete (Long id, Principal principal);
}
