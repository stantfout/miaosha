package com.usth.miaosha.service;

import com.usth.miaosha.dao.GoodsDao;
import com.usth.miaosha.domain.Goods;
import com.usth.miaosha.domain.MiaoshaOrder;
import com.usth.miaosha.domain.MiaoshaUser;
import com.usth.miaosha.domain.OrderInfo;
import com.usth.miaosha.redis.MiaoshaKey;
import com.usth.miaosha.redis.RedisService;
import com.usth.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减库存
        boolean success = goodsService.reduceStock(goods);
        if(success) {
            //写入秒杀订单
            return orderService.createOrder(user,goods);
        } else {
            setGoodsOver(goods.getId());
            return null;
        }
    }

    /**
     * orderId : 成功 |
     * -1 : 库存不足 |
     * 0 : 排队中 |
     */
    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        if(order != null) {
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver,"" + goodsId,true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, "" + goodsId);
    }
}
