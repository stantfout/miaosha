package com.usth.miaosha.controller;

import com.sun.org.apache.bcel.internal.classfile.Code;
import com.usth.miaosha.domain.MiaoshaOrder;
import com.usth.miaosha.domain.MiaoshaUser;
import com.usth.miaosha.domain.OrderInfo;
import com.usth.miaosha.rabbitmq.MQSender;
import com.usth.miaosha.rabbitmq.MiaoshaMessage;
import com.usth.miaosha.redis.GoodsKey;
import com.usth.miaosha.redis.RedisService;
import com.usth.miaosha.result.CodeMsg;
import com.usth.miaosha.result.Result;
import com.usth.miaosha.service.GoodsService;
import com.usth.miaosha.service.MiaoshaService;
import com.usth.miaosha.service.OrderService;
import com.usth.miaosha.vo.GoodsVo;
import org.apache.ibatis.executor.ResultExtractor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    private Map<Long,Boolean> localOverMap = new HashMap<>();

    /**
     * 系统初始化
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList == null) {
            return;
        }
        for (GoodsVo goods : goodsList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock,"" + goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(),false);
        }
    }

    /**
     * 优化前: QPS:973 (5000 * 10)
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    /**
     * GET POST有什么区别
     * GET幂等
     **/
    @RequestMapping(value = "/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model, MiaoshaUser user,
                       @RequestParam("goodsId") long goodsId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //内存标记,减少redis访问
        Boolean over = localOverMap.get(goodsId);
        if(over) {
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //预减库存
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if(stock < 0) {
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队
        MiaoshaMessage message = new MiaoshaMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);
        mqSender.sendMiaoshaMessage(message);
        return Result.success(0);//排队中
        /*
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //减库存，下订单，写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user,goods);
        return Result.success(orderInfo);
         */
    }

    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(MiaoshaUser user,
                                         @RequestParam("goodsId") long goodsId) {
        if(user == null) {
            return  Result.error(CodeMsg.SESSION_ERROR);
        }
        long result =  miaoshaService.getMiaoshaResult(user.getId(),goodsId);
        return Result.success(result);
    }
}
