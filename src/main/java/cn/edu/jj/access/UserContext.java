package cn.edu.jj.access;

import cn.edu.jj.domain.MiaoshaUser;

public class UserContext {

    private static ThreadLocal<MiaoshaUser> userHolder;

    static {
        userHolder = new ThreadLocal<>();
    }

    public static void setUser(MiaoshaUser user) {
        userHolder.set(user);
    }

    public static MiaoshaUser getUser() {
        return userHolder.get();
    }

}
