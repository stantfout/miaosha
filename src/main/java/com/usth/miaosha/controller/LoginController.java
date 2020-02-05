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
    public Result<Boolean> doLogin(LoginVo loginVo) {
        log.info(loginVo.toString());
        //参数校验
        String passInput = loginVo.getPassword();
        String mobile = loginVo.getMobile();
        if(StringUtils.isEmpty(passInput)) {
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        if(StringUtils.isEmpty(mobile)) {
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        if(!ValidatorUtil.isMobile(mobile)) {
            return Result.error(CodeMsg.MOBILE_ERROR);
        }
        CodeMsg codeMsg = userService.login(loginVo);
        if(codeMsg.getCode() == 0) {
            return Result.success(true);
        } else {
            return Result.error(codeMsg);
        }
    }


}
