package cn.edu.jj.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MiaoshaGoodsMapper {
    @Update("UPDATE miaosha_goods SET stock_count = stock_count - 1 WHERE goods_id = #{goodsId} AND stock_count>0")
    int reduceStockById(@Param("goodsId") Long goodsId);

    @Delete("DELETE FROM miaosha_user")
    void deleteAllUser();
}
