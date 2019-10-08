package com.annie.auth.redis;

import com.annie.auth.util.CommonConst;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.lang.Nullable;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;


/**
 * Created by Administrator on 2017/8/25.
 */
@SuppressWarnings("unchecked")
public class MySessionRedisDAO extends EnterpriseCacheSessionDAO {

    /**
     * 用户登录的session超时时长，24小时
     */
    private final static long TIME_OUT_SECONDS = 24 * 60 * 60L;

    private ValueOperations redisOp;

    public MySessionRedisDAO(RedisTemplate redisTemplate) {
        redisOp = redisTemplate.opsForValue();
    }

    /**
     * 创建session
     *
     * @param session
     * @return
     */
    protected Serializable doCreate(Session session) {
        Serializable sessionId = super.doCreate(session);
        //在这里创建session到redis中
        redisOp.set(CommonConst.USER_SESSION_KEY_PRE + sessionId.toString(), session, TIME_OUT_SECONDS, TimeUnit.SECONDS);
        return sessionId;
    }

    /**
     * 读取session
     *
     * @param sessionId
     * @return
     */
    protected Session doReadSession(Serializable sessionId) {
        Session session = super.doReadSession(sessionId);
        if (session == null) {
            //在这里从redis中获取session(当内存中找不到sessionId的session时候)
            session = (Session) redisOp.get(CommonConst.USER_SESSION_KEY_PRE + sessionId.toString());
        }
        return session;
    }

    /**
     * 每个该session的请求都会调用
     * 更新session
     *
     * @param session
     */
    protected void doUpdate(Session session) {
        super.doUpdate(session);
        //这里更新redis用户信息过期时间
        Boolean res = (Boolean) redisOp.getOperations().execute(new RedisCallback<Object>() {
            @Nullable
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.set(redisOp.getOperations().getKeySerializer().serialize(CommonConst.USER_SESSION_KEY_PRE + session.getId().toString()), redisOp.getOperations().getValueSerializer().serialize(session), Expiration.seconds(TIME_OUT_SECONDS), RedisStringCommands.SetOption.SET_IF_PRESENT);
            }
        });
        if (!res) {
            session.stop();
        }
    }

    /**
     * 70f36102-07ed-48dd-a64e-ce7de3860012
     * 删除session
     *
     * @param session
     */
    protected void doDelete(Session session) {
        super.doDelete(session);
        //这里从redis里面移除
        redisOp.getOperations().delete(CommonConst.USER_SESSION_KEY_PRE + session.getId().toString());
    }
}
