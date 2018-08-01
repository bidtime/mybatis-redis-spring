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

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mybatis.caches.JUnitTestBase;
import org.mybatis.caches.callback.IObjectCallBack;
import org.mybatis.caches.callback.IResultCallBack;
import org.mybatis.caches.utils.TaskRunning2;
import org.mybatis.caches.utils.ThreadComm;
import org.mybatis.caches.utils.ThreadUtils;

/**
 * Test with Ubuntu sudo apt-get install redis-server execute the test
 */
public final class RedisTaskRunningCase2 extends JUnitTestBase {

  @BeforeClass
  public static void newCache() {
  }

  @AfterClass
  public static void clearCache() {
    TaskRunning2.clear();
  }

  //  @Test
  //  public void shouldLock() throws Exception {
  //    for (int i=0; i<100; i++) {
  //      Boolean b = cache.lock("my", i);
  //      System.out.println(i + ": " + b);
  //    }
  //  }

  @Test
  public void test_lock() throws Exception {
    ThreadUtils.thread(ThreadComm.POOL_SIZE, new IObjectCallBack<Thread>() {

      @Override
      public void callback(Thread thread) throws Exception {
        for (int j = 0; j < ThreadComm.APPLE; j++) {
          String orgKey = String.valueOf(j);
          //
          TaskRunning2.lockOnly(orgKey, orgKey + "-task", new IResultCallBack<Long>() {

            @Override
            public Long callback() throws Exception {
              return null;
            }
            
          }, 0);
        }
      }
    });
  }

  @Test
  public void test_lock_orgId() throws Exception {
    List<Long> ll = ThreadComm.getOrgList();
    ThreadUtils.thread(ThreadComm.POOL_SIZE, new IObjectCallBack<Thread>() {

      @Override
      public void callback(Thread thread) throws Exception {
        for (int j = 0; j < ThreadComm.APPLE; j++) {
          String orgKey = "insertInventoryDaily_" + ll.get(j);
          //
          TaskRunning2.lockUnlock(orgKey, orgKey + "-task", new IResultCallBack<Long>() {

            @Override
            public Long callback() throws Exception {
              return null;
            }
            
          }, 0);
        }
      }
    });
  }

  public void insertInventoryDaily() throws Exception {
    List<Long> myList = new ArrayList<>();
    List<Long> all = ThreadComm.getOrgList();
    for (int i = 0; i < all.size(); i++) {
      Long orgId = all.get(i);
      TaskRunning2.lockOnly("insertInventoryDaily_" + orgId, "进销存台帐", new IResultCallBack<Long>() {

        @Override
        public Long callback() throws Exception {
          myList.add(orgId);
          return orgId;
        }
      }, 0);
    }
  }

}
