package com.dong.easyeval.utils;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class RedisTool {

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    /**
     * 尝试获取分布式锁
     * @param jedis Redis客户端
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {
        SetParams setParams = new SetParams();
        setParams.ex(expireTime);
        setParams.nx();

        String result = jedis.set(lockKey, requestId, setParams); //执行redis的set扩展命令
        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

}