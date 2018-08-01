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
package org.mybatis.caches.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mybatis.caches.callback.IObjectCallBack;

/**
 * Test with Ubuntu sudo apt-get install redis-server execute the test
 */
public final class ThreadUtils {

  public static void executor(int poolSize, IObjectCallBack<ExecutorService> cb) throws Exception {
    System.out.println("begin...");
    //
    ExecutorService cachedThreadPool = Executors.newFixedThreadPool(poolSize);
    for (int i = 0; i < poolSize; i++) {
      if (cb != null) {
        cb.callback(cachedThreadPool);
      }
    }
    //
    cachedThreadPool.shutdown();    // 关闭线程池
    //
    while (true) {
      if (cachedThreadPool.isTerminated()) {
        System.out.println("all thread terminated.");
        break;
      }
      Thread.sleep(0);
    }
    //
    System.out.println("end.");
  }

  public static void thread(int poolSize, IObjectCallBack<Thread> cb) throws Exception {
    ThreadUtils.executor(poolSize, new IObjectCallBack<ExecutorService>() {

      @Override
      public void callback(ExecutorService pool) throws Exception {
        pool.execute(new Runnable() {

          @Override
          public void run() {
            if (cb != null) {
              try {
                cb.callback(Thread.currentThread());
              } catch (Exception e) {
              }
            }
          }
        });
      }
    });
  }

  //  public static void dd() throws Exception {
  //    ExecutorService cachedThreadPool = Executors.newFixedThreadPool(poolSize);
  //    for (int i = 0; i < poolSize; i++) {
  //      cachedThreadPool.execute(new Runnable() {
  //
  //        @Override
  //        public void run() {
  //          if (cb != null) {
  //            cb.callback(Thread.currentThread());
  //          }
  //        }
  //      });
  //      Thread.sleep(0);
  //    }
  //    //
  //    cachedThreadPool.shutdown(); // 关闭线程池
  //    //
  //    while (true) {
  //      if (cachedThreadPool.isTerminated()) {
  //        System.out.println("all thread terminated.");
  //        break;
  //      }
  //      Thread.sleep(0);
  //    }
  //    System.out.println("end.");
  //  }

}
