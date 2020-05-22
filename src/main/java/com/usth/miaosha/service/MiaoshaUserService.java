package com.usth.miaosha.service;

import com.alibaba.druid.util.StringUtils;
import com.usth.miaosha.dao.MiaoshaUserDao;
import com.usth.miaosha.domain.MiaoshaUser;
import com.usth.miaosha.domain.User;
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
        //取缓存
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if(user != null) {
            return user;
        }
        //取数据库
        user = miaoshaUserDao.getById(id);
        if(user != null) {
            redisService.set(MiaoshaUserKey.getById,""+id,user);
        }
        return user;
    }

    public boolean updatePassword(String token,long id, String password) {
        //取user
        MiaoshaUser user = getById(id);
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //https://blog.csdn.net/tTU1EvLDeLFq5btqik/article/details/78693323
        //更新数据库
        MiaoshaUser updateUser = new MiaoshaUser();
        updateUser.setId(id);
        updateUser.setPassword(MD5Util.fromPassToDBPass(password,user.getSalt()));
        miaoshaUserDao.updatePassword(updateUser);
        //修改缓存
        redisService.delete(MiaoshaUserKey.getById,""+id);
        user.setPassword(updateUser.getPassword());
        redisService.set(MiaoshaUserKey.token,token,user);
        return true;
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

    public String reg(HttpServletResponse response, LoginVo loginVo){
        if(loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        MiaoshaUser user = getById(Long.parseLong(loginVo.getMobile()));
        if (user != null) {
            throw new GlobalException(CodeMsg.REG_ERROR);
        }
        user = new MiaoshaUser();
        user.setId(Long.valueOf(loginVo.getMobile()));
        user.setLoginCount(1);
        user.setNickname("user");
        user.setRegisterDate(new Date());
        user.setSalt(UUIDUtil.uuid());
        user.setPassword(MD5Util.inputPassToDBPass(loginVo.getPassword(), user.getSalt()));
        miaoshaUserDao.insertUser(user);
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
            miaoshaUserDao.insertUser(user);
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
