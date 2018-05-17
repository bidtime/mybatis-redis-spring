package org.mybatis.caches.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;

public class RedisCacheTransfer {

  @Autowired
  public void setRedisConnectionFactory(RedisConnectionFactory factory) {
    RedisCaches.setRedisConnectionFactory(factory);
  }

  @Autowired
  public void setCluster(boolean cluster) {
    RedisCaches.setCluster(cluster);
  }

  @Autowired
  public void setSerializer(String value) throws Exception {
    if ("kryo".equalsIgnoreCase(value)) {
      RedisCaches.setSerializer(KryoSerializer.INSTANCE);
    } else if (!"jdk".equalsIgnoreCase(value)) {
      // Custom serializer is not supported yet.
      throw new Exception("Unknown serializer: '" + value + "'");
    }
  }

}
