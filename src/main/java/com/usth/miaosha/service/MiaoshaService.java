package com.usth.miaosha.service;

import com.usth.miaosha.dao.GoodsDao;
import com.usth.miaosha.domain.Goods;
import com.usth.miaosha.domain.MiaoshaUser;
import com.usth.miaosha.domain.OrderInfo;
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

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减库存
        boolean success = goodsService.reduceStock(goods);
        if(success) {
            //写入秒杀订单
            return orderService.createOrder(user,goods);
        } else {
            return null;
        }
    }
}
