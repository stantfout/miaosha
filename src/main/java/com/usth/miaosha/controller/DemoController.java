package com.usth.miaosha.controller;

import com.usth.miaosha.domain.User;
import com.usth.miaosha.rabbitmq.MQSender;
import com.usth.miaosha.redis.RedisService;
import com.usth.miaosha.redis.UserKey;
import com.usth.miaosha.result.CodeMsg;
import com.usth.miaosha.result.Result;
import com.usth.miaosha.service.UserService;
import org.apache.ibatis.executor.ResultExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }
    //1.rest api json输出 2.页面
    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello() {
        return Result.success("hello,imooc");
        // return new Result(0, "success", "hello,imooc");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloError() {
        return Result.error(CodeMsg.SERVER_ERROR);
        //return new Result(500102, "XXX");
    }

    @RequestMapping("/thymeleaf")
    public String  thymeleaf(Model model) {
        model.addAttribute("name", "Joshua");
        return "hello";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx() {
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        User user = redisService.get(UserKey.getById,"1", User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisGSet() {
        User user = new User();
        user.setId(1);
        user.setName("1900");
        redisService.set(UserKey.getById,"1",user);
        return Result.success(true);
    }

    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq() {
        sender.send("hello world");
        return Result.success("hello world");
    }

    @RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> topic() {
        sender.sendTopic("hello world");
        return Result.success("hello world");
    }

    @RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> fanout() {
        sender.sendFanout("hello world");
        return Result.success("hello world");
    }

    @RequestMapping("/mq/header")
    @ResponseBody
    public Result<String> header() {
        sender.sendHeader("hello world");
        return Result.success("hello world");
    }
}
