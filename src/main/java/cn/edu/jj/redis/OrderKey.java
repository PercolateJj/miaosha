package cn.edu.jj.redis;

public class OrderKey extends BasePrefix {


    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("mo");
}


