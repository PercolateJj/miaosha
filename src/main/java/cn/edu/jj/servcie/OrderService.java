package cn.edu.jj.servcie;

import cn.edu.jj.domain.MiaoshaOrder;
import cn.edu.jj.domain.OrderInfo;

public interface OrderService {
    MiaoshaOrder findByUserIdGoodsId(Long userId, Long goodsId);
    Long insertOrder(OrderInfo orderInfo);

    OrderInfo findById(long orderId);
}
