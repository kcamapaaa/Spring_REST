package com.vladislav.filestoragerest.controller;

import com.vladislav.filestoragerest.dto.AdminUserDto;
import com.vladislav.filestoragerest.dto.UserDto;
import com.vladislav.filestoragerest.model.User;
import com.vladislav.filestoragerest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users/")
public class UserRestControllerV1 {
    private final UserService userService;

    @Autowired
    public UserRestControllerV1(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserDto>> getAllUsers() {
        List<User> allUsers = userService.getAll();
        if(allUsers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<AdminUserDto> usersDtoList = allUsers.stream().map(AdminUserDto::fromUser).toList();
        return new ResponseEntity<>(usersDtoList, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<AdminUserDto> getUserByID(@PathVariable(name = "id") Long id) {
        User user = userService.getById(id);
        if(user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        AdminUserDto userDto = AdminUserDto.fromUser(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("myInfo")
    public ResponseEntity<UserDto> getMyInfo(Principal principal) {
        User usersInfo = userService.getMyInfo(principal);
        if(usersInfo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserDto userDto = UserDto.fromUser(usersInfo);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<AdminUserDto> updateUser(@RequestBody User user) {
        User updatedUser = userService.update(user);
        if(updatedUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        AdminUserDto userDto = AdminUserDto.fromUser(updatedUser);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable(name = "id") Long id) {
        boolean deleted = userService.delete(id);
        if(deleted) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }
}
