package com.vladislav.filestoragerest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladislav.filestoragerest.model.Status;
import com.vladislav.filestoragerest.model.User;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminUserDto {
    private Long id;
    private String username;
    private String status;

    public User toUser() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setStatus(Status.valueOf(status));
        return user;
    }

    public static AdminUserDto fromUser(User user) {
        AdminUserDto adminUserDto = new AdminUserDto();
        adminUserDto.setId(user.getId());
        adminUserDto.setUsername(user.getUsername());
        adminUserDto.setStatus(user.getStatus().name());
        return adminUserDto;
    }
}
