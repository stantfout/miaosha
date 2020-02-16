package com.usth.miaosha.service;

import com.usth.miaosha.dao.OrderDao;
import com.usth.miaosha.domain.MiaoshaOrder;
import com.usth.miaosha.domain.MiaoshaUser;
import com.usth.miaosha.domain.OrderInfo;
import com.usth.miaosha.redis.OrderKey;
import com.usth.miaosha.redis.RedisService;
import com.usth.miaosha.util.MD5Util;
import com.usth.miaosha.util.UUIDUtil;
import com.usth.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired(required = false)
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, "" + userId + "_" + goodsId, MiaoshaOrder.class);
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(user.getId());
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsCount(1);
        orderInfo.setCreateDate(new Date());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setOrderStatus(0);
        orderDao.insertOrder(orderInfo);
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);

        redisService.set(OrderKey.getMiaoshaOrderByUidGid,"" + user.getId() + "_" + goods.getId(), miaoshaOrder);
        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }

    public String createOrderPayPath(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <= 0) {
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(OrderKey.getOrderPath,"" + user.getId() + "_" + goodsId, str);
        return str;
    }

    public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
        if(user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(OrderKey.getOrderPath,"" + user.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    public long payOrder(MiaoshaUser user, long goodsId,long orderId) {
        orderDao.deleteMiaoshaOrder(user.getId(),goodsId);
        orderDao.deleteOrder(user.getId(),orderId);
        redisService.set(OrderKey.deleteOrder,"" + user.getId() + "_" + goodsId,goodsId);
        return 1;
    }
}
