package com.usth.miaosha.redis;

public interface KeyPrefix {
    int expireSeconds();

    String getPrefix();
}
