package cn.edu.jj.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        String realKey = prefix.getPrefix() + key;
        BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(realKey);
        String str = (String) boundValueOperations.get();
        T t = stringToBean(str, clazz);
        return t;
    }

    public <T> Boolean set(KeyPrefix prefix, String key, T value) {
        String str = beanToString(value);
        if (str == null || str.length() <= 0) {
            return false;
        }
        //生成真正的key
        String realKey = prefix.getPrefix() + key;
        BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(realKey);
        int seconds = prefix.expireSeconds();
        if (seconds <= 0) {
            boundValueOperations.set(str);
        } else {
            boundValueOperations.set(str, seconds, TimeUnit.SECONDS);
        }
        return true;
    }

    public <T> Boolean exists(KeyPrefix prefix, String key) {
        //生成真正的key
        String realKey = prefix.getPrefix() + key;
        return redisTemplate.hasKey(realKey);
    }

    public <T> Long incr(KeyPrefix prefix, String key) {
        //生成真正的key
        String realKey = prefix.getPrefix() + key;
        BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(realKey);
        return boundValueOperations.increment();
    }

    public <T> Long decr(KeyPrefix prefix, String key) {
        //生成真正的key
        String realKey = prefix.getPrefix() + key;
        BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(realKey);
        return boundValueOperations.decrement();
    }

    public static <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    public Boolean delete(BasePrefix prefix, String key) {
        String realKey = prefix.getPrefix() + key;
        Boolean delete = redisTemplate.delete(realKey);
        return delete;
    }
}