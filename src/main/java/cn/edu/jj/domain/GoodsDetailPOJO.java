package cn.edu.jj.domain;

public class GoodsDetailPOJO {
    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsPOJO goods;
    private MiaoshaUser user;

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsPOJO getGoods() {
        return goods;
    }

    public void setGoods(GoodsPOJO goods) {
        this.goods = goods;
    }

    public MiaoshaUser getUser() {
        return user;
    }

    public void setUser(MiaoshaUser user) {
        this.user = user;
    }
}
