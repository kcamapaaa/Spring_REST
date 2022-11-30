package com.vladislav.filestoragerest.service;

import com.vladislav.filestoragerest.model.User;

public interface UserService extends GenericService<User, Long> {
    User register(User user);
    User update(User user);
    User getByUsername(String username);
}
