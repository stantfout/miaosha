package com.usth.miaosha.service;

import com.usth.miaosha.dao.MiaoshaUserDao;
import com.usth.miaosha.domain.MiaoshaUser;
import com.usth.miaosha.result.CodeMsg;
import com.usth.miaosha.util.MD5Util;
import com.usth.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoshaUserService {

    @Autowired(required = false)
    MiaoshaUserDao miaoshaUserDao;

    public MiaoshaUser getById(long id) {
        return miaoshaUserDao.getById(id);
    }

    public CodeMsg login(LoginVo loginVo) {
        if(loginVo == null) {
            return CodeMsg.SERVER_ERROR;
        }
        String mobile = loginVo.getMobile();
        String fromPass = loginVo.getPassword();
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if(user == null) {
            return CodeMsg.MOBILE_NOT_EXIST;
        }
        String dbPass = user.getPassword();
        String salt = user.getSalt();
        String calcPass = MD5Util.fromPassToDBPass(fromPass, salt);
        if(!calcPass.equals(dbPass)) {
            return CodeMsg.PASSWORD_ERROR;
        }
        return CodeMsg.SUCCESS;
    }
}
