/**
 *    Copyright 2015-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.caches.redis;

import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;

/**
 * 本源码摘自 mybatis-redis 项目，并加以改造
 * 
 * 项目 mybatis-redis 地址：https://github.com/mybatis/redis-cache.git
 * 
 * Cache adapter for Redis.
 *
 * @author riverbo
 */
public final class RedisCaches implements Cache {

  private static final Logger log = LoggerFactory.getLogger(RedisCaches.class);

  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

  private String id;
  
  private byte[] idBytes;

  private Integer timeout;

  public RedisCaches(final String id) {
    if (id == null) {
      throw new IllegalArgumentException("Cache instances require an ID");
    }
    this.id = id;
    this.idBytes = id.getBytes();
    if (log.isDebugEnabled()) {
      log.debug("cache: {}", id);
    }
  }

  public RedisCaches(final String id, Integer timeout) {
    this(id);
    this.timeout = timeout;
  }

  public RedisCaches(Integer timeout) {
    this("");
    this.timeout = timeout;
  }

  private <T> T execute(RedisCallback<T> callback) {
    return RedisCacheTransfer.execute(callback);
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public int getSize() {
    return (Integer) execute(new RedisCallback<Integer>() {

      @Override
      public Integer doInRedis(RedisConnection conn) {
        return conn.dbSize().intValue();
      }
    });
  }

  @Override
  public void putObject(final Object key, final Object value) {
    execute(new RedisCallback<Object>() {

      @SuppressWarnings("rawtypes")
      @Override
      public Object doInRedis(RedisConnection conn) {
        Boolean success = null;
        final byte[] keyBytes = key.toString().getBytes();
        byte[] dataBytes = null;
        if (value != null) {
          dataBytes = RedisCacheTransfer.serialize(value);
        }
        success = conn.hSet(idBytes, keyBytes, dataBytes);
        if (timeout != null && conn.ttl(idBytes) == -1) {
          conn.expire(idBytes, timeout);
        }
        if (log.isDebugEnabled()) {
          if (value != null) {
            if (value instanceof Collection) {
              log.debug("hSet: {}-{}, {}:size({}), tm({})", id, key, success, ((Collection)value).size(), timeout);
            } else {
              log.debug("hSet: {}-{}, {}, tm({})", id, key, success, timeout);
            }
          } else {
            log.debug("hSet: {}-{}, {}, tm({})", id, key, success, timeout);
          }
        }
        return null;
      }
    });
  }
  
//  private static final String LOCK_SUCCESS = "OK";
//  private static final String SET_IF_NOT_EXIST = "NX";
//  private static final String SET_WITH_EXPIRE_TIME = "PX";

  /**
   * 尝试获取分布式锁
   * @param jedis Redis客户端
   * @param lockKey 锁
   * @param requestId 请求标识
   * @param expireTime 超期时间
   * @return 是否获取成功
   */
//  public static boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {
//
//      String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
//
//      if (LOCK_SUCCESS.equals(result)) {
//          return true;
//      }
//      return false;
//
//  }
  
  public boolean lock(final Object key, final Object value) {
    return execute(new RedisCallback<Boolean>() {

      @SuppressWarnings("rawtypes")
      @Override
      public Boolean doInRedis(RedisConnection conn) {
        Boolean success = null;
        final byte[] keyBytes = key.toString().getBytes();
        byte[] dataBytes = null;
        if (value != null) {
          dataBytes = RedisCacheTransfer.serialize(value);
        }
        success = conn.hSetNX(idBytes, keyBytes, dataBytes);
        if (timeout != null && conn.ttl(idBytes) == -1) {
          conn.expire(idBytes, timeout);
        }
        if (log.isDebugEnabled()) {
          if (value != null) {
            if (value instanceof Collection) {
              log.debug("hSetNX: {}-{}, {}:size({}), tm({})", id, key, success, ((Collection)value).size(), timeout);
            } else {
              log.debug("hSetNX: {}-{}, {}, tm({})", id, key, success, timeout);
            }
          } else {
            log.debug("hSetNX: {}-{}, {}, tm({})", id, key, success, timeout);
          }
        }
        return success;
      }
    });
  }
  
  @Override
  public Object getObject(final Object key) {
    return execute(new RedisCallback<Object>() {

      @SuppressWarnings("rawtypes")
      @Override
      public Object doInRedis(RedisConnection conn) {
        final byte[] keyBytes = key.toString().getBytes();
        Object result = null;
        final byte[] dataBytes = conn.hGet(idBytes, keyBytes);
        if (dataBytes != null) {
          result = RedisCacheTransfer.unserialize(dataBytes);
        }
        if (log.isDebugEnabled()) {
          if (result != null) {
            if (result instanceof Collection) {
              log.debug("hGet: {}-{}, {}:size({})", id, key, result.getClass().getSimpleName(), ((Collection)result).size());
            } else {
              log.debug("hGet: {}-{}, {}", id, key, result.getClass().getSimpleName());
            }
          } else {
            log.debug("hGet: {}-{}, {}: is null", id, key, (result == null) ? null : result.getClass().getSimpleName());
          }
        }
        return result;
      }
    });
  }

  @Override
  public Object removeObject(final Object key) {
    return execute(new RedisCallback<Long>() {

      @Override
      public Long doInRedis(RedisConnection conn) {
        final byte[] keyBytes = key.toString().getBytes();
        Long l = conn.hDel(idBytes, keyBytes);
        if (log.isDebugEnabled()) {
          log.debug("hDel: {}-{}, {}", id, key, (l == null) ? null : l);
        }        
        return l;
      }
    });
  }

  public boolean unlock(final Object key) {
    Object l = removeObject(key);
    return (l != null) ? true : false;
  }

  @Override
  public void clear() {
    execute(new RedisCallback<Object>() {

      @Override
      public Object doInRedis(RedisConnection conn) {
        if (conn == null) {
          return null;
        }
        Long l = conn.del(idBytes);
        if (log.isDebugEnabled()) {
          log.debug("clear: {}, {}", id, (l == null) ? null : l);
        }        
        return null;
      }
    });

  }

  @Override
  public ReadWriteLock getReadWriteLock() {
    return readWriteLock;
  }

  @Override
  public String toString() {
    return "Redis {" + id + "}";
  }

  // properties

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  public Integer getTimeout() {
    return timeout;
  }

}