package com.usth.miaosha.service;

import com.alibaba.druid.util.StringUtils;
import com.usth.miaosha.dao.MiaoshaUserDao;
import com.usth.miaosha.domain.MiaoshaUser;
import com.usth.miaosha.exception.GlobalException;
import com.usth.miaosha.redis.MiaoshaUserKey;
import com.usth.miaosha.redis.RedisService;
import com.usth.miaosha.result.CodeMsg;
import com.usth.miaosha.util.MD5Util;
import com.usth.miaosha.util.UUIDUtil;
import com.usth.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Service
public class MiaoshaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired(required = false)
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoshaUser getById(long id) {
        return miaoshaUserDao.getById(id);
    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        if(user != null) {
            addCookie(response,token,user);
        }
        return user;
    }

    public String login(HttpServletResponse response,LoginVo loginVo) {
        if(loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String fromPass = loginVo.getPassword();
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        String dbPass = user.getPassword();
        String salt = user.getSalt();
        String calcPass = MD5Util.fromPassToDBPass(fromPass, salt);
        if(!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response,token,user);
        return token;
    }

    public void reg(){
        for(int i=0;i<5000;i++) {
            MiaoshaUser user = new MiaoshaUser();
            user.setId(13000000000L+i);
            user.setLoginCount(1);
            user.setNickname("user"+i);
            user.setRegisterDate(new Date());
            user.setSalt("1a2b3c");
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            miaoshaUserDao.insetrUser(user);
        }
    }

    private void addCookie(HttpServletResponse response,String token,MiaoshaUser user) {
        redisService.set(MiaoshaUserKey.token,token,user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
