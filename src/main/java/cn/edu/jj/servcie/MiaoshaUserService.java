package cn.edu.jj.servcie;

import cn.edu.jj.domain.LoginPOJO;
import cn.edu.jj.domain.MiaoshaUser;
import cn.edu.jj.result.CodeMsg;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface MiaoshaUserService {
    boolean updatePassword(String token, long id, String formPass);

    MiaoshaUser findById(Long id);

    List<MiaoshaUser> findAll();

    Boolean login(HttpServletResponse response, LoginPOJO loginPOJO);

    MiaoshaUser findByToken(HttpServletResponse response, String token);
}
