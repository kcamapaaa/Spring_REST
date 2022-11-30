package com.vladislav.filestoragerest.service;

import com.vladislav.filestoragerest.model.File;
import com.vladislav.filestoragerest.model.User;

public interface FileService extends GenericService<File, Long>{
    File save(File file);
    File getByFileName(String fileName);
}
