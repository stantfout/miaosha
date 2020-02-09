package com.usth.miaosha.controller;

import com.usth.miaosha.domain.MiaoshaUser;
import com.usth.miaosha.result.Result;
import com.usth.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model,MiaoshaUser user) {
        model.addAttribute("user",user);
        return Result.success(user);
    }

    @RequestMapping("/init")
    @ResponseBody
    public Result<Boolean> init() {
        miaoshaUserService.reg();
        return Result.success(true);
    }
}
