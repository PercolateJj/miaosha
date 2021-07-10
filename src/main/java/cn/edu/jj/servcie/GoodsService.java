package cn.edu.jj.servcie;

import cn.edu.jj.domain.GoodsPOJO;

import java.util.List;

public interface GoodsService {
    List<GoodsPOJO> findAll();

    GoodsPOJO findById(Long id);

    Boolean reduceStock(GoodsPOJO goods);
}
