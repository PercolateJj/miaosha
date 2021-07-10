package cn.edu.jj.mapper;

import cn.edu.jj.domain.GoodsPOJO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsMapper {
    @Select("select g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date from goods g left join miaosha_goods mg on g.id=mg.goods_id")
    List<GoodsPOJO> findAll();

    @Select("select g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date from goods g left join miaosha_goods mg on g.id=mg.goods_id where g.id = #{id}")
    GoodsPOJO findById(@Param("id") Long id);
}
