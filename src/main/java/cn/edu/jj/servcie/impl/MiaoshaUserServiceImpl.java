package cn.edu.jj.servcie.impl;

import cn.edu.jj.domain.LoginPOJO;
import cn.edu.jj.domain.MiaoshaUser;
import cn.edu.jj.exception.GlobalException;
import cn.edu.jj.mapper.MiaoshaUserMapper;
import cn.edu.jj.redis.MiaoshaUserKey;
import cn.edu.jj.redis.RedisService;
import cn.edu.jj.result.CodeMsg;
import cn.edu.jj.servcie.MiaoshaUserService;
import cn.edu.jj.util.MD5Util;
import cn.edu.jj.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class MiaoshaUserServiceImpl implements MiaoshaUserService {

    @Autowired
    private MiaoshaUserMapper miaoshaUserMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public List<MiaoshaUser> findAll() {
        return miaoshaUserMapper.findAll();
    }

    public static final String COOKIE_NAME_TOKEN = "token";

    @Override
    public MiaoshaUser findById(Long id) {
        //取缓存
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if (user != null) {
            return user;
        }
        //取数据库
        user = miaoshaUserMapper.findById(id);
        if (user != null) {
            redisService.set(MiaoshaUserKey.getById, "" + id, user);
        }
        return user;
    }

    public boolean updatePassword(String token, long id, String formPass) {
        //取user
        MiaoshaUser user = findById(id);
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formMd5FromNet(formPass, user.getSalt()));
        miaoshaUserMapper.updatePassword(toBeUpdate);
        //处理缓存
        if (!redisService.delete(MiaoshaUserKey.getById, "" + id)) {
            return false;
        }
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token, token, user);
        return true;
    }

    @Override
    public Boolean login(HttpServletResponse response, LoginPOJO loginPOJO) {
        if (loginPOJO == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginPOJO.getMobile();
        String password = loginPOJO.getPassword();
        MiaoshaUser user = miaoshaUserMapper.findById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        String dbPass = user.getPassword();
        String dbSalt = user.getSalt();
        String calPass = MD5Util.formMd5FromNet(loginPOJO.getPassword(), dbSalt);
        if (!calPass.equals(dbPass)) {
            System.out.println("calPass:" + calPass);
            System.out.println("dbPass:" + dbPass);
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return true;
    }

    @Override
    public MiaoshaUser findByToken(HttpServletResponse response, String token) {
        if (token == null) {
            return null;
        }
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        if (miaoshaUser != null) {
            // 延长有效期
            addCookie(response, token, miaoshaUser);
        }
        return miaoshaUser;
    }

    private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
        redisService.set(MiaoshaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
