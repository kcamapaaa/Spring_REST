package com.vladislav.filestoragerest.service.impl;

import com.vladislav.filestoragerest.model.*;
import com.vladislav.filestoragerest.repository.EventRepository;
import com.vladislav.filestoragerest.repository.FileRepository;
import com.vladislav.filestoragerest.service.EventService;
import com.vladislav.filestoragerest.service.FileService;
import com.vladislav.filestoragerest.service.StorageService;
import com.vladislav.filestoragerest.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.security.Principal;
import java.util.List;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    FileRepository fileRepository;
    StorageService storageService;
    UserService userService;
    EventService eventService;

    @Autowired
    public FileServiceImpl(@Lazy FileRepository fileRepository, StorageService storageService, UserService userService, EventService eventService) {
        this.fileRepository = fileRepository;
        this.storageService = storageService;
        this.userService = userService;
        this.eventService = eventService;
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
    @Transactional
    public boolean delete(Long id, Principal principal) {
        File fileById = fileRepository.findById(id).orElse(null);
        if(fileById == null) {
            log.warn("IN delete - no file found by id: {}", id);
            return false;
        } else {
            fileById.setStatus(Status.DELETED);
            File deletedFile = fileRepository.save(fileById);
            log.info("IN deleted - file {} found by id: {}", deletedFile, id);
            storageService.deleteFile(fileById.getFileName());
            User user = userService.getByUsername(principal.getName());
            if(user == null) {
                throw new RuntimeException("Unknown user");
            }
            eventService.save(new Event(Action.DELETED, user, fileById));
            return true;
        }
    }

    @Override
    @Transactional
    public File save(MultipartFile file, Principal principal) {
        User user = userService.getByUsername(principal.getName());
        if(user == null) {
            throw new RuntimeException("Unknown user");
        }
        String linkToFile = storageService.uploadFile(file);
        File newFile = new File(linkToFile, file.getOriginalFilename());
        File savedFile = fileRepository.save(newFile);
        eventService.save(new Event(Action.UPLOADED, user, savedFile));
        log.info("IN save - file with id: {} was saved", savedFile.getId());
        return savedFile;
    }
}
