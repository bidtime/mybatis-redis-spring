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

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mybatis.caches.JUnitTestBase;
import org.mybatis.caches.callback.IObjectCallBack;
import org.mybatis.caches.utils.ThreadComm;
import org.mybatis.caches.utils.ThreadUtils;

/**
 * Test with Ubuntu sudo apt-get install redis-server execute the test
 */
public final class RedisLockTestCase extends JUnitTestBase {

  private static final String DEFAULT_ID = "z-jss-REDIS-1";

  private static RedisCaches cache;

  @BeforeClass
  public static void newCache() {
    cache = new RedisCaches(DEFAULT_ID);
  }

  @AfterClass
  public static void clearCache() {
    cache.clear();
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
      public void callback(Thread thread) {
        for (int j = 0; j < ThreadComm.APPLE; j++) {
          String orgKey = String.valueOf(j);
          StringBuilder sb = new StringBuilder();
          try {
            Boolean b = cache.lock(orgKey, 1);
            //System.out.println(thread.getName() + ": " + "lock( " + b + ")");
            sb.append(thread.getName() + ": " + orgKey + ", " + "lock( " + b + "), ");
            if (b) {
              cache.unlock(orgKey);
              //System.out.println(thread.getName() + ": " + "unlock( " + b + ")");
              sb.append("unlock: " + b);
              System.out.println(sb.toString());
            } else {
              //sb.append("unlock: none");
            }
            Thread.sleep(0);
          } catch (Exception e) {} finally {
            sb.setLength(0);
            sb = null;
          }
        }
      }
    });
  }
  
  @Test
  public void test_lock_orgId() throws Exception {
    List<Long> ll = ThreadComm.getOrgList();
    cache.clear();
    ThreadUtils.thread(ThreadComm.POOL_SIZE, new IObjectCallBack<Thread>() {

      @Override
      public void callback(Thread thread) {
        for (int j = 0; j < ll.size(); j++) {
          String orgKey = "insertInventoryDaily_" + ll.get(j);
          StringBuilder sb = new StringBuilder();
          try {
            Boolean b = cache.lock(orgKey, 1);
            //System.out.println(thread.getName() + ": " + "lock( " + b + ")");
            sb.append(thread.getName() + ": " + orgKey + ", " + "lock( " + b + "), ");
            if (b) {
              cache.unlock(orgKey);
              //System.out.println(thread.getName() + ": " + "unlock( " + b + ")");
              sb.append("unlock: " + b);
              System.out.println(sb.toString());
            } else {
              //sb.append("unlock: none");
            }
            Thread.sleep(0);
          } catch (Exception e) {
          } finally {
            sb.setLength(0);
            sb = null;
          }
        }
      }
    });
  }

  //  public void insertInventoryDaily() throws Exception {
  //    List<Long> orgListAll = new ArrayList<>();
  //    ll.add(172775312319356928L);
  //    ll.add(190925610435780608L);
  //    ll.add(190925610435780608L);
  //    ll.add(190929618428796928L);
  //    ll.add(190930284723347456L);
  //    ll.add(190930987462205440L);
  //    ll.add(190933831552638976L);
  //    ll.add(190935885020962816L);
  //    ll.add(197295811213471744L);
  //    ll.add(199813557787860992L);
  //      for (int i=0; i<orgListAll.size(); i++) {
  //        ApOrgAllListBO u = orgListAll.get(i);
  //        TaskRunning.doIt("insertInventoryDaily_" + u.getOrgId(), "进销存台帐", new IResultCallBack<Long>() {
  //
  //          @Override
  //          public Long callback() throws Exception {
  //            myList.add(u);
  //            return u.getOrgId();
  //          }
  //        }, 2000);
  //      }
  //    }
  //  }

}
