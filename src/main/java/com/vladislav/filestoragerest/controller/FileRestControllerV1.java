package com.vladislav.filestoragerest.controller;

import com.vladislav.filestoragerest.dto.AdminFileDto;
import com.vladislav.filestoragerest.dto.FileDto;
import com.vladislav.filestoragerest.model.File;
import com.vladislav.filestoragerest.service.EventService;
import com.vladislav.filestoragerest.service.FileService;
import com.vladislav.filestoragerest.service.StorageService;
import com.vladislav.filestoragerest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files/")
public class FileRestControllerV1 {

    private final FileService fileService;
    private final StorageService storageService;
    private final EventService eventService;
    private final UserService userService;

    @Autowired
    public FileRestControllerV1(FileService fileService, @Lazy StorageService storageService, EventService eventService, UserService userService) {
        this.fileService = fileService;
        this.storageService = storageService;
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<AdminFileDto>> getAllFiles() {
        List<File> allFiles = fileService.getAll();
        if (allFiles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<AdminFileDto> allFilesDto = allFiles.stream().map(AdminFileDto::fromFile).toList();
        return new ResponseEntity<>(allFilesDto, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<AdminFileDto> getFileById(@PathVariable(value = "id") Long id) {
        File fileById = fileService.getById(id);
        if (fileById == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        AdminFileDto fileDto = AdminFileDto.fromFile(fileById);
        return new ResponseEntity<>(fileDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FileDto> uploadFile(@RequestParam(value = "file") MultipartFile file, Principal principal) {
        File savedFile = fileService.save(file, principal);
        FileDto fileDto = FileDto.fromFile(savedFile);
        return new ResponseEntity<>(fileDto, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Boolean> deleteFileById(@PathVariable(value = "id") Long id, Principal principal) {
        boolean deleted = fileService.delete(id, principal);
        if(deleted) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }
}
