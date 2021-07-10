package cn.edu.jj.servcie.impl;

import cn.edu.jj.domain.MiaoshaOrder;
import cn.edu.jj.domain.OrderInfo;
import cn.edu.jj.mapper.OrderMapper;
import cn.edu.jj.servcie.MiaoshaService;
import cn.edu.jj.servcie.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private MiaoshaService miaoshaService;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public MiaoshaOrder findByUserIdGoodsId(Long userId, Long goodsId) {
        return miaoshaService.findByUserIdGoodsId(userId, goodsId);
    }

    @Override
    public Long insertOrder(OrderInfo orderInfo) {
        orderMapper.insertOrder(orderInfo);
        return orderInfo.getId();
    }

    @Override
    public OrderInfo findById(long orderId) {
        return orderMapper.findById(orderId);
    }
}
