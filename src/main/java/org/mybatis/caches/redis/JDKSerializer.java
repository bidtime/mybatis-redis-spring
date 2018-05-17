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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum JDKSerializer implements Serializer {
  
  //Enum singleton, which is preferred approach since Java 1.5
  INSTANCE;

  private JDKSerializer() {
    // prevent instantiation
  }

  public byte[] serialize(Object object) {
    ObjectOutputStream oos = null;
    ByteArrayOutputStream baos = null;
    try {
      baos = new ByteArrayOutputStream();
      oos = new ObjectOutputStream(baos);
      oos.writeObject(object);
      return baos.toByteArray();
    } catch (Exception e) {
      log.error("serial:{}, {}", object.getClass().getName(), e.getMessage());
      return null;
    }
  }

  public Object unserialize(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    ByteArrayInputStream bais = null;
    try {
      bais = new ByteArrayInputStream(bytes);
      ObjectInputStream ois = new ObjectInputStream(bais);
      return ois.readObject();
    } catch (Exception e) {
      log.error("unserial:{}, {}", bytes, e.getMessage());
      return null;
    }
  }

  private static final Logger log = LoggerFactory.getLogger(JDKSerializer.class);

//  public static byte[] serialize(Object object) {
//    ObjectOutputStream oos = null;
//    ByteArrayOutputStream baos = null;
//    try {
//      // 序列化
//      baos = new ByteArrayOutputStream();
//      oos = new ObjectOutputStream(baos);
//      oos.writeObject(object);
//      byte[] bytes = baos.toByteArray();
//      return bytes;
//    } catch (Exception e) {
//      log.error("serial:{}, {}", object.getClass().getName(), e.getMessage());
//    }
//    return null;
//  }
//
//  public static Object unserialize(byte[] bytes) {
//    ByteArrayInputStream bais = null;
//    try {
//      // 反序列化
//      bais = new ByteArrayInputStream(bytes);
//      ObjectInputStream ois = new ObjectInputStream(bais);
//      return ois.readObject();
//    } catch (Exception e) {
//      log.error("unserial:{}, {}", bytes, e.getMessage());
//    }
//    return null;
//  }

}
