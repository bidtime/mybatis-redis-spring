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

/**
 * 
 * @author riverbo copy from spring redis
 * @since 2018.05.05
 */
public interface Serializer {

  /**
   * Serialize method
   * @param object
   * @return serialized bytes
   */
  public byte[] serialize(Object object);

  /**
   * Unserialize method
   * @param bytes
   * @return unserialized object
   */
  public Object unserialize(byte[] bytes);

}
