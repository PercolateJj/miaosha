package cn.edu.jj.servcie.impl;

import cn.edu.jj.domain.GoodsPOJO;
import cn.edu.jj.mapper.GoodsMapper;
import cn.edu.jj.mapper.MiaoshaGoodsMapper;
import cn.edu.jj.servcie.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private MiaoshaGoodsMapper miaoshaGoodsMapper;

    @Override
    public List<GoodsPOJO> findAll() {
        return goodsMapper.findAll();
    }

    @Override
    public GoodsPOJO findById(Long id) {
        return goodsMapper.findById(id);
    }

    @Override
    public Boolean reduceStock(GoodsPOJO goods) {
        int i = miaoshaGoodsMapper.reduceStockById(goods.getId());
        return i != 0;
    }
}
