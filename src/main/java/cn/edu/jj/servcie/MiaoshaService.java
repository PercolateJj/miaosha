package cn.edu.jj.servcie;

import cn.edu.jj.domain.GoodsPOJO;
import cn.edu.jj.domain.MiaoshaOrder;
import cn.edu.jj.domain.MiaoshaUser;
import cn.edu.jj.domain.OrderInfo;

import java.awt.image.BufferedImage;

public interface MiaoshaService {
    OrderInfo miaosha(MiaoshaUser user, GoodsPOJO goods);

    long getMiaoshaResult(Long userId, long goodsId);

    MiaoshaOrder findByUserIdGoodsId(Long userId, Long goodsId);

    boolean checkPath(Long id, Long goodsId, String path);

    String createMiaoshaPath(Long id, Long goodsId);

    BufferedImage createVerifyCode(MiaoshaUser user, long goodsId);

    boolean checkVerifyCode(MiaoshaUser user, Long goodsId, Integer verifyCode);
}

