package cn.edu.jj.domain;

import cn.edu.jj.validator.IsMobile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class LoginPOJO {
    @NotNull
    @IsMobile
    private String mobile;
    @NotNull
    private String password;

    @Override
    public String toString() {
        return "LoginPOJO{" +
                "mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
