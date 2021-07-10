package cn.edu.jj.domain;

public class OrderDetailPOJO {
    private GoodsPOJO goods;
    private OrderInfo order;

    public GoodsPOJO getGoods() {
        return goods;
    }

    public void setGoods(GoodsPOJO goods) {
        this.goods = goods;
    }

    public OrderInfo getOrder() {
        return order;
    }

    public void setOrder(OrderInfo order) {
        this.order = order;
    }
}
