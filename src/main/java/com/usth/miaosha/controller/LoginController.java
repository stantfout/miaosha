package com.usth.miaosha.controller;

import com.usth.miaosha.result.Result;
import com.usth.miaosha.service.MiaoshaUserService;
import com.usth.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
public class LoginController {

    @Autowired
    private MiaoshaUserService userService;

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/")
    public String init() {
        return "login";
    }

    @RequestMapping("/login/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/login/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        String token = userService.login(response, loginVo);
        return Result.success(token);
    }

    @RequestMapping("/login/do_reg")
    @ResponseBody
    public Result<String> doReg(HttpServletResponse response,@Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        String token = userService.reg(response,loginVo);
        return Result.success(token);
    }

}
