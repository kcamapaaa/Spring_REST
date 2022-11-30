package com.vladislav.filestoragerest.controller;

import com.vladislav.filestoragerest.dto.UserDto;
import com.vladislav.filestoragerest.model.Action;
import com.vladislav.filestoragerest.model.Event;
import com.vladislav.filestoragerest.model.File;
import com.vladislav.filestoragerest.model.User;
import com.vladislav.filestoragerest.service.EventService;
import com.vladislav.filestoragerest.service.FileService;
import com.vladislav.filestoragerest.service.StorageService;
import com.vladislav.filestoragerest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users/")
public class UserRestControllerV1 {
    private final UserService userService;
    private final StorageService storageService;
    private final FileService fileService;
    private final EventService eventService;

    @Autowired
    public UserRestControllerV1(UserService userService, StorageService storageService, FileService fileService, EventService eventService) {
        this.userService = userService;
        this.storageService = storageService;
        this.fileService = fileService;
        this.eventService = eventService;
    }

    @GetMapping()
    public ResponseEntity<UserDto> getUserByUsername(Principal principal) {
        User user = userService.getByUsername(principal.getName());
        UserDto result = UserDto.fromUser(user);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("files/upload")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file, Principal principal) {
        String link = storageService.uploadFile(file);
        if(link == null) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
        File savedFile = saveFileInDataBase(link, file.getOriginalFilename());
        saveEventInDataBase(savedFile, Action.UPLOADED, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("files/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName, Principal principal) {
        File byFileName = fileService.getByFileName(fileName);
        saveEventInDataBase(byFileName, Action.DOWNLOADED, principal);
        ByteArrayResource byteArrayResource = storageService.downloadFile(fileName);
        if(byteArrayResource == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(byteArrayResource, HttpStatus.OK);
    }

    @DeleteMapping("files/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName, Principal principal) {
        File fileToDelete = fileService.getByFileName(fileName);
        if(fileToDelete == null) {
            return new ResponseEntity<>("Not deleted", HttpStatus.NOT_FOUND);
        }
        boolean deleted = fileService.delete(fileToDelete.getId());
        if(!deleted) {
            return new ResponseEntity<>("Not deleted", HttpStatus.FORBIDDEN);
        }
        storageService.deleteFile(fileName);
        saveEventInDataBase(fileToDelete, Action.DELETED, principal);
        return new ResponseEntity<>(storageService.deleteFile(fileName), HttpStatus.OK);
    }

    private File saveFileInDataBase(String link, String fileName) {
        File savedFile = new File();
        savedFile.setFileName(fileName);
        savedFile.setLocation(link);
        return fileService.save(savedFile);
    }

    private void saveEventInDataBase(File file, Action action, Principal principal) {
        User currentUser = userService.getByUsername(principal.getName());
        Event event = new Event();
        event.setFile(file);
        event.setUser(currentUser);
        event.setAction(action);
        eventService.save(event);
    }
}
