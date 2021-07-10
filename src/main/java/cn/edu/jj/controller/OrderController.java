package cn.edu.jj.controller;

import cn.edu.jj.domain.GoodsPOJO;
import cn.edu.jj.domain.MiaoshaUser;
import cn.edu.jj.domain.OrderDetailPOJO;
import cn.edu.jj.domain.OrderInfo;
import cn.edu.jj.redis.RedisService;
import cn.edu.jj.result.CodeMsg;
import cn.edu.jj.result.Result;
import cn.edu.jj.servcie.GoodsService;
import cn.edu.jj.servcie.MiaoshaUserService;
import cn.edu.jj.servcie.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailPOJO> info(Model model, MiaoshaUser user,
                                        @RequestParam("orderId") long orderId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.findById(orderId);
        if(order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsPOJO goods = goodsService.findById(goodsId);
        OrderDetailPOJO vo = new OrderDetailPOJO();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }

}
