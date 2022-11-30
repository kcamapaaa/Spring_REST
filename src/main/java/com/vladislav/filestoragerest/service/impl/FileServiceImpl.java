package com.vladislav.filestoragerest.service.impl;

import com.vladislav.filestoragerest.model.File;
import com.vladislav.filestoragerest.model.Status;
import com.vladislav.filestoragerest.model.User;
import com.vladislav.filestoragerest.repository.FileRepository;
import com.vladislav.filestoragerest.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    FileRepository fileRepository;

    @Autowired
    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public List<File> getAll() {
        List<File> allFiles = fileRepository.findAll();
        log.info("IN getAll - {} files found", allFiles.size());
        return allFiles.isEmpty() ? null : allFiles;
    }

    @Override
    public File getById(Long id) {
        File file = fileRepository.findById(id).orElse(null);
        if(file == null) {
            log.warn("IN getById - no file found by id: {}" , id);
        }
        log.info("IN getById - file: {} found by id: {}", file, id);
        return file;
    }

    @Override
    public File getByFileName(String fileName) {
        File result = fileRepository.findByFileName(fileName);
        log.info("IN getByFileName - file: {} found by fileName: {}", result, fileName);
        return result;
    }

    @Override
    public boolean delete(Long id) {
        File file = fileRepository.findById(id).orElse(null);
        if (file != null) {
            file.setStatus(Status.DELETED);
            File deletedFile = fileRepository.save(file);
            log.info("IN deleted - file {} found by id: {}", deletedFile, id);
            return true;
        } else {
            log.warn("IN delete - no file found by id: {}", id);
            return false;
        }
    }

    @Override
    public File save(File file) {
        File savedFile = fileRepository.save(file);
        log.info("IN save - file with id: {} was saved", savedFile.getId());
        return savedFile;
    }
}
