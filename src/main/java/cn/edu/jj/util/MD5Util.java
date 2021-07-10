package cn.edu.jj.util;


import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    private static final String salt = "1a2b3c4d";

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    public static String formMd5FromPassword(String password) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + password + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String formMd5FromNet(String password, String saltDB) {
        String str = "" + saltDB.charAt(0) + saltDB.charAt(2) + password + saltDB.charAt(5) + saltDB.charAt(4);
        return md5(str);
    }

//    public static void main(String[] args) {
//        String netPass = formMd5FromPassword("jinjian");
//        String dbPass = formMd5FromNet(netPass, "a1b2c3");
//        System.out.println(netPass);
//        System.out.println(dbPass);
//    }
}
