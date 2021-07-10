package cn.edu.jj.controller;

import cn.edu.jj.domain.MiaoshaUser;
import cn.edu.jj.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @RequestMapping("info")
    public Result<MiaoshaUser> info(MiaoshaUser user, Model model) {
        return Result.success(user);
    }
}
