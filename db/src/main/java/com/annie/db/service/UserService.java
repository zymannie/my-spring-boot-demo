package com.annie.db.service;

import com.annie.db.basic.service.BaseService;
import com.annie.db.dao.User;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<User> {

    public User getUserInfo(String username) {
        User user = new User();
        user.setUsername("annie");
        user.setPassword("123456");
        return user;
    }
}
