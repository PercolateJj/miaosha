package cn.edu.jj.servcie.impl;

import cn.edu.jj.domain.GoodsPOJO;
import cn.edu.jj.domain.MiaoshaOrder;
import cn.edu.jj.domain.MiaoshaUser;
import cn.edu.jj.domain.OrderInfo;
import cn.edu.jj.exception.GlobalException;
import cn.edu.jj.mapper.MiaoshaOrderMapper;
import cn.edu.jj.rabbitmq.MQConfig;
import cn.edu.jj.redis.MiaoshaKey;
import cn.edu.jj.redis.OrderKey;
import cn.edu.jj.redis.RedisService;
import cn.edu.jj.result.CodeMsg;
import cn.edu.jj.servcie.GoodsService;
import cn.edu.jj.servcie.MiaoshaService;
import cn.edu.jj.servcie.OrderService;
import cn.edu.jj.util.MD5Util;
import cn.edu.jj.util.UUIDUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Random;

@Service
public class MiaoshaServiceImpl implements MiaoshaService {
    @Autowired
    MiaoshaOrderMapper mapper;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    // 减库存、下订单、写入秒杀订单
    @Transactional
    @Override
    public OrderInfo miaosha(MiaoshaUser user, GoodsPOJO goods) {
        Boolean success = goodsService.reduceStock(goods);
        if (!success) {
            setGoodsOver(goods.getId());
            throw new GlobalException(CodeMsg.MIAO_SHA_OVER);
        }
        OrderInfo order = createOrder(user, goods);
        return order;
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsPOJO goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(user.getId());
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setCreateDate(new Date());
        Long orderId = orderService.insertOrder(orderInfo);

        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setUserId(user.getId());
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderId);
        mapper.insertOrder(miaoshaOrder);
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + user.getId() + "_" + goods.getId(), miaoshaOrder);
        return orderInfo;
    }

    @Override
    public MiaoshaOrder findByUserIdGoodsId(Long userId, Long goodsId) {
        //return mapper.findByUserIdGoodsId(userId, goodsId);
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, "" + userId + "_" + goodsId, MiaoshaOrder.class);
    }

    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder order = orderService.findByUserIdGoodsId(userId, goodsId);
        if (order != null) {//秒杀成功
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        AMQP.Queue.DeclareOk declareOk = rabbitTemplate.execute(new ChannelCallback<AMQP.Queue.DeclareOk>() {
            public AMQP.Queue.DeclareOk doInRabbit(Channel channel) throws Exception {
                return channel.queueDeclarePassive(MQConfig.MIAOSHA_QUEUE_NAME);
            }
        });
        return redisService.exists(MiaoshaKey.isGoodsOver, "" + goodsId) && declareOk.getMessageCount() == 0;
    }

//    public void reset(List<GoodsPOJO> goodsList) {
//        goodsService.resetStock(goodsList);
//        orderService.deleteOrders();
//    }

    public String createMiaoshaPath(Long userId, Long goodsId) {
        if (userId == null || goodsId == null) {
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid());
        redisService.set(MiaoshaKey.getMiaoshaPath, "" + userId + "_" + goodsId, str);
        return str;
    }

    @Override
    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, "" + user.getId() + "_" + goodsId, rnd);
        //输出图片
        return image;
    }

    @Override
    public boolean checkVerifyCode(MiaoshaUser user, Long goodsId, Integer verifyCode) {
        if (user == null || goodsId == null || verifyCode == null) {
            return false;
        }
        boolean flag = verifyCode.equals(
                redisService.get(MiaoshaKey.getMiaoshaVerifyCode, "" + user.getId() + "_" + goodsId, Integer.class)
        );
        if (flag) {
            redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, "" + user.getId() + "_" + goodsId);
            return true;
        } else {
            return false;
        }
    }

    private static char[] ops = new char[]{'+', '-', '*'};

    /**
     * + - *
     */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = "" + num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    public boolean checkPath(Long id, Long goodsId, String path) {
        if (goodsId == null || path == null) {
            return false;
        }
        return path.equals(redisService.get(MiaoshaKey.getMiaoshaPath, "" + id + "_" + goodsId, String.class));
    }

    public boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) {
        if (user == null || goodsId <= 0) {
            return false;
        }
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId, Integer.class);
        if (codeOld == null || codeOld - verifyCode != 0) {
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId);
        return true;
    }

    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
