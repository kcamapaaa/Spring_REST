package com.vladislav.filestoragerest.controller;

import com.vladislav.filestoragerest.dto.AdminEventDto;
import com.vladislav.filestoragerest.dto.AdminUserDto;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/")
public class AdminRestControllerV1 {
    private final UserService userService;
    private final StorageService storageService;
    private final FileService fileService;
    private final EventService eventService;

    @Autowired
    public AdminRestControllerV1(UserService userService, StorageService storageService, FileService fileService, EventService eventService) {
        this.userService = userService;
        this.storageService = storageService;
        this.fileService = fileService;
        this.eventService = eventService;
    }

    @GetMapping("users/{id}")
    public ResponseEntity<AdminUserDto> getUserById(@PathVariable("id") Long id) {
        User userById = userService.getById(id);

        if(userById == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        AdminUserDto userDto = AdminUserDto.fromUser(userById);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("users")
    public ResponseEntity<List<AdminUserDto>> getAllUsers() {
        List<User> allUsers = userService.getAll();
        if(allUsers == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<AdminUserDto> dtoUsers = allUsers.stream().map(AdminUserDto::fromUser).toList();
        return new ResponseEntity<>(dtoUsers, HttpStatus.OK);
    }

    @DeleteMapping("users/{id}")
    public ResponseEntity<String> deleteUserByID(@PathVariable("id") Long id) {
        boolean deleted = userService.delete(id);
        if(deleted) {
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("Not deleted", HttpStatus.NOT_FOUND);
    }

    @PutMapping("users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User updatedUser = userService.update(user);
        if(updatedUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
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

    @GetMapping("events")
    public ResponseEntity<List<AdminEventDto>> getAllEvents() {
        List<Event> allEvents = eventService.getAll();
        if(allEvents == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<AdminEventDto> eventsDto = allEvents.stream().map(AdminEventDto::fromEvent).toList();
        return new ResponseEntity<>(eventsDto, HttpStatus.OK);
    }

    @GetMapping("events/{id}")
    public ResponseEntity<AdminEventDto> getEventById(@PathVariable("id") Long id) {
        Event eventByID = eventService.getById(id);
        if(eventByID == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        AdminEventDto adminEventDto = AdminEventDto.fromEvent(eventByID);
        return new ResponseEntity<>(adminEventDto, HttpStatus.OK);
    }

    @DeleteMapping("events/delete/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable("id") Long id) {
        boolean deleted = eventService.delete(id);
        if(deleted) {
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("Not deleted", HttpStatus.NOT_FOUND);
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
