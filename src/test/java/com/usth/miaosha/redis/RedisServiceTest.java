package com.usth.miaosha.redis;

import com.usth.miaosha.domain.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class RedisServiceTest {

    @Autowired
    RedisService redisService;

    @Test
    public void get() {
        User user = redisService.get(UserKey.getById, "1", User.class);
        System.out.println(user);
    }

    @Test
    public void set() {
        User user = new User();
        user.setName("111");
        user.setId(1);
        redisService.set(UserKey.getById,"1",user);
    }
}