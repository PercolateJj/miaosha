package cn.edu.jj.controller;

import cn.edu.jj.domain.LoginPOJO;
import cn.edu.jj.domain.MiaoshaUser;
import cn.edu.jj.redis.MiaoshaUserKey;
import cn.edu.jj.result.CodeMsg;
import cn.edu.jj.result.Result;
import cn.edu.jj.servcie.MiaoshaUserService;
import cn.edu.jj.servcie.impl.MiaoshaUserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/miaosha")
public class LoginController {
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private MiaoshaUserService userService;

    @RequestMapping("/login")
    public String toLogin(Model model, MiaoshaUser user) {
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        return "redirect:/goods/to_list";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginPOJO loginPOJO) throws Exception {
        log.info(loginPOJO.toString());
        //登录
        userService.login(response, loginPOJO);
        return Result.success(true);
    }
}
