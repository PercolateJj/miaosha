package cn.edu.jj.controller;

import cn.edu.jj.access.AccessLimit;
import cn.edu.jj.domain.GoodsPOJO;
import cn.edu.jj.domain.MiaoshaOrder;
import cn.edu.jj.domain.MiaoshaUser;
import cn.edu.jj.rabbitmq.MQSender;
import cn.edu.jj.rabbitmq.MiaoshaMessage;
import cn.edu.jj.redis.GoodsKey;
import cn.edu.jj.redis.RedisService;
import cn.edu.jj.result.CodeMsg;
import cn.edu.jj.result.Result;
import cn.edu.jj.servcie.GoodsService;
import cn.edu.jj.servcie.MiaoshaService;
import cn.edu.jj.servcie.MiaoshaUserService;
import cn.edu.jj.servcie.OrderService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    MQSender sender;

    private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    /**
     * 优化前
     * 5000 * 10
     * QPS 2237.3
     *
     * 优化后
     * 5000 * 10
     * QPS 6788.9
     */

    /**
     * GET POST有什么区别？
     * GET 幂等，多次调用结果一致
     * POST 会对服务器数据进行修改
     */
    @RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model, MiaoshaUser user,
                                @RequestParam("goodsId") long goodsId,
                                @PathVariable("path") String path) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // 验证path
        boolean checkPath = miaoshaService.checkPath(user.getId(), goodsId, path);

        if (!checkPath) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }


        // 内存标记减少redis访问
        Boolean aBoolean = localOverMap.get(goodsId);
        if (aBoolean) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.findByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);//10
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        MiaoshaMessage message = new MiaoshaMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(message);
        return Result.success(0);
    }

    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoshaUser user,
                                      @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }

    /**
     * 初始化Redis缓存
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsPOJO> goodsList = goodsService.findAll();
        if (goodsList == null) {
            return;
        }
        for (GoodsPOJO goods : goodsList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
    }

    @AccessLimit(seconds = 5, maxCount = 5)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(MiaoshaUser user,
                                         @RequestParam("goodsId") long goodsId,
                                         @RequestParam(value = "verifyCode", required = false, defaultValue = "-99999999") Integer verifyCode,
                                         HttpServletRequest request) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        /**
         // 接口限流 废弃，转由拦截器实现，查看access包。
         String url = request.getRequestURI();
         String key = "" + url + "_" + user.getId();
         Integer count = redisService.get(AccessKey.withExpire(5), key, Integer.class);
         if (count == null) {
         redisService.set(AccessKey.withExpire(5), key, 1);
         } else if (count < 5) {
         redisService.incr(AccessKey.withExpire(5), key);
         } else {
         return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
         }
         */

        boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path = miaoshaService.createMiaoshaPath(user.getId(), goodsId);
        return Result.success(path);
    }


    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getVerifyCode(Model model, MiaoshaUser user,
                                        @RequestParam("goodsId") long goodsId,
                                        HttpServletResponse response) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image = miaoshaService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }
}
