package com.usth.miaosha.controller;

import com.alibaba.druid.util.StringUtils;
import com.usth.miaosha.result.CodeMsg;
import com.usth.miaosha.result.Result;
import com.usth.miaosha.service.MiaoshaUserService;
import com.usth.miaosha.util.ValidatorUtil;
import com.usth.miaosha.vo.LoginVo;
import org.codehaus.groovy.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private MiaoshaUserService userService;

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(@Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        userService.login(loginVo);
        return Result.success(true);
    }


}
