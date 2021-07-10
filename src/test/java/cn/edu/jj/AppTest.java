package cn.edu.jj;

import static org.junit.Assert.assertTrue;

import cn.edu.jj.domain.MiaoshaUser;
import cn.edu.jj.mapper.MiaoshaGoodsMapper;
import cn.edu.jj.mapper.MiaoshaUserMapper;
import cn.edu.jj.redis.MiaoshaUserKey;
import cn.edu.jj.redis.RedisService;
import cn.edu.jj.util.MD5Util;
import cn.edu.jj.util.UUIDUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class AppTest {

    @Autowired
    private MiaoshaUserMapper miaoshaUserMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MiaoshaGoodsMapper miaoshaGoodsMapper;

    @Test
    public void testMiaoshaGoodsMapper() {
        int i = miaoshaGoodsMapper.reduceStockById(1l);
        System.out.println(i);
    }

    @Test
    public void generateData() {
        miaoshaGoodsMapper.deleteAllUser();

        try {
            OutputStream out = new FileOutputStream("/home/jj/config");
            for (int i = 0; i < 5000; i++) {
                long id = 13000000000l + i;
                String nickName = "jinjian" + String.valueOf(i);
                String password = "jinjian" + String.valueOf(i);
                String salt = "1a2b3c4d";
                MiaoshaUser user = new MiaoshaUser();
                user.setId(id);
                user.setNickname(nickName);
                user.setPassword(MD5Util.formMd5FromNet(MD5Util.formMd5FromPassword(password), salt));
                user.setSalt(salt);
                miaoshaUserMapper.insertUser(user);

                String uuid = UUIDUtil.uuid();
                redisService.set(MiaoshaUserKey.token, uuid, user);
                String tmp = String.valueOf(id) + ',' + uuid + '\n';
                out.write(tmp.getBytes());
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
