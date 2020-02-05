package com.usth.miaosha.service;

import com.usth.miaosha.dao.UserDao;
import com.usth.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired(required = false)
    UserDao userDao;

    public User getById(int id) {
        return userDao.getById(id);
    }

    @Transactional
    public boolean tx() {
        User user1 = new User();
        user1.setId(5);
        user1.setName("555");
        userDao.insert(user1);

        User user2 = new User();
        user2.setId(15);
        user2.setName("555");
        userDao.insert(user2);

        return true;
    }
}
