package com.usth.miaosha.redis;

public class OrderKey extends BasePrefix{

    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey(0,"moug");
    public static OrderKey deleteOrder = new OrderKey(600,"moug");
    public static OrderKey getOrderPath = new OrderKey(60,"op");
}
