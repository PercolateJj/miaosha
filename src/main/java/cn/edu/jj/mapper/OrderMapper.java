package cn.edu.jj.mapper;

import cn.edu.jj.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderMapper {
    @Insert("INSERT INTO order_info(user_id,goods_id,goods_name,goods_count,goods_price,create_date) VALUES(#{userId},#{goodsId},#{goodsName},#{goodsCount},#{goodsPrice},#{createDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertOrder(OrderInfo orderInfo);

    @Select("SELECT * FROM order_info WHERE id = #{orderId}")
    OrderInfo findById(@Param("orderId") long orderId);
}
