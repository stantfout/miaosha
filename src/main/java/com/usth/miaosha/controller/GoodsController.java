package com.usth.miaosha.controller;

import com.alibaba.druid.util.StringUtils;
import com.usth.miaosha.domain.MiaoshaUser;
import com.usth.miaosha.redis.GoodsKey;
import com.usth.miaosha.redis.RedisService;
import com.usth.miaosha.result.Result;
import com.usth.miaosha.service.GoodsService;
import com.usth.miaosha.service.MiaoshaUserService;
import com.usth.miaosha.vo.GoodsDetailVo;
import com.usth.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String toList(HttpServletRequest request,
                         HttpServletResponse response,
                         Model model, MiaoshaUser user) {
        model.addAttribute("user",user);
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if(!StringUtils.isEmpty(html)) {
            return html;
        }
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList",goodsList);
        //手动渲染
        SpringWebContext context = new SpringWebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap(),applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",context);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList,"",html);
        }
        return html;
    }

    @RequestMapping(value = "/to_detail/{goodsId}",produces = "text/html")
    @ResponseBody
    public String toDetail(HttpServletRequest request,
                           HttpServletResponse response,
                           Model model, MiaoshaUser user,
                           @PathVariable Long goodsId) {
        model.addAttribute("user",user);

        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
        if(!StringUtils.isEmpty(html)) {
            return html;
        }

        //查询商品详细信息
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus;
        /*
         * 剩余开始时间
         */
        int remainSeconds;

        if(now < startAt) {//秒杀未开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)(startAt - now) / 1000;
        } else if(now > endAt) {//秒杀结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);

        //手动渲染
        SpringWebContext context = new SpringWebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap(),applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail",context);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
        }
        return html;
    }

    @RequestMapping(value="/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request,
                                           HttpServletResponse response,
                                           Model model, MiaoshaUser user,
                                           @PathVariable Long goodsId) {
        //查询商品详细信息
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus;
        /*
         * 剩余开始时间
         */
        int remainSeconds;

        if(now < startAt) {//秒杀未开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)(startAt - now) / 1000;
        } else if(now > endAt) {//秒杀结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setMiaoshaStatus(miaoshaStatus);
        vo.setRemainSeconds(remainSeconds);
        return Result.success(vo);
    }
}
