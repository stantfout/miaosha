package com.usth.miaosha.controller;

import com.usth.miaosha.access.AccessLimit;
import com.usth.miaosha.domain.MiaoshaUser;
import com.usth.miaosha.domain.OrderInfo;
import com.usth.miaosha.redis.RedisService;
import com.usth.miaosha.result.CodeMsg;
import com.usth.miaosha.result.Result;
import com.usth.miaosha.service.GoodsService;
import com.usth.miaosha.service.OrderService;
import com.usth.miaosha.vo.GoodsVo;
import com.usth.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @AccessLimit(seconds = 0,maxCount = -1)
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Result<OrderDetailVo> detail(@RequestParam("orderId") long orderId) {
        OrderInfo order = orderService.getOrderById(orderId);
        if(order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        Long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setGoods(goods);
        vo.setOrder(order);
        return Result.success(vo);
    }

    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getOrderPayPath(MiaoshaUser user,
                                         @RequestParam("goodsId") long goodsId) {
        //创建用户支付路径
        String path = orderService.createOrderPayPath(user,goodsId);
        return Result.success(path);
    }

    @RequestMapping(value = "/{path}/payOrder", method = RequestMethod.POST)
    @ResponseBody
    public Result<Long> orderPay(MiaoshaUser user,
                                   @RequestParam("goodsId") long goodsId,
                                   @RequestParam("orderId") long orderId,
                                   @PathVariable String path) {
        boolean check = orderService.checkPath(user,goodsId,path);
        if(!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        long result = orderService.payOrder(user, goodsId,orderId);
        return Result.success(result);
    }
}
