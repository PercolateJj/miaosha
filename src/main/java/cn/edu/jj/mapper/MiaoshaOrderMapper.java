package cn.edu.jj.mapper;

import cn.edu.jj.domain.MiaoshaOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MiaoshaOrderMapper {
    @Insert("INSERT INTO miaosha_order(user_id,order_id,goods_id) VALUES(#{userId},#{orderId},#{goodsId})")
    void insertOrder(MiaoshaOrder miaoshaOrder);

    @Select("SELECT * FROM miaosha_order WHERE user_id = #{userId} AND goods_id=#{goodsId}")
    MiaoshaOrder findByUserIdGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);
}
