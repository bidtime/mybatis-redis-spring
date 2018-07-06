package org.mybatis.caches.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;

public class RedisCacheTransfer {
  
  private static final Logger log = LoggerFactory.getLogger(RedisCacheTransfer.class);

  /**
   * 与 spring-data-redis 的 RedisConnectionFactory，结合使用
   */
  private static RedisConnectionFactory factory;
  
  static private Serializer serializer = JDKSerializer.INSTANCE;

  /*
   * redis 服务器 是否使用集群
   */
  private static boolean cluster = false;

  public static byte[] serialize(Object object) {
    return serializer.serialize(object);
  }

  public static Object unserialize(byte[] bytes) {
    return serializer.unserialize(bytes);
  }

  public static <T> T execute(RedisCallback<T> callback) {
    RedisConnection conn = null;
    try {
      if (!cluster) {
        conn = RedisCacheTransfer.factory.getConnection();
      } else {
        conn = RedisCacheTransfer.factory.getClusterConnection();
      }
    } catch (Exception e) {
      log.error("get connection: {}, {}", e.getMessage(), e.getStackTrace());
      return null;
    }
    
    if (conn == null) {
      return null;
    }

    try {
      return (T) callback.doInRedis(conn);
    } catch (Exception e) {
      log.error("exceute: {}, {}", e.getMessage(), e.getStackTrace());
      return null;
    } finally {
      conn.close();
    }
  }
  
  // set props

  @Autowired
  public void setRedisConnectionFactory(RedisConnectionFactory factory) {
    log.debug("factory: {}", factory.getClass().getSimpleName());
    RedisCacheTransfer.factory = factory;
  }

  @Autowired
  public void setCluster(boolean cluster) {
    log.debug("cluster: {}", cluster);
    RedisCacheTransfer.cluster = cluster;
  }

  @Autowired
  public void setSerializer(String value) throws Exception {
    log.debug("serializer: {}", value);
    if ("kryo".equalsIgnoreCase(value)) {
      RedisCacheTransfer.serializer = KryoSerializer.INSTANCE;
    } else if (!"jdk".equalsIgnoreCase(value)) {
      // Custom serializer is not supported yet.
      throw new Exception("Unknown serializer: '" + value + "'");
    }
  }

}
