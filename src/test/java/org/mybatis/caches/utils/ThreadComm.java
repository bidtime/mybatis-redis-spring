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

import java.util.ArrayList;
import java.util.List;

/**
 * Test with Ubuntu sudo apt-get install redis-server execute the test
 */
public final class ThreadComm {

  public static final int POOL_SIZE = 1;

  public static final int APPLE = 16;

  public static List<Long> getOrgList() {
    List<Long> ll = new ArrayList<>();
    for (int i = 0; i < ThreadComm.APPLE; i++) {
      long b = (int) i + 1;
      ll.add(b);
    }
    //
    return ll;
  }

  //private List<Long> getOrgList() {
  //  List<Long> ll = new ArrayList<>();
  //  ll.add(172775312319356928L);
  //  ll.add(190925610435780608L);
  //  ll.add(190925610435780608L);
  //  ll.add(190929618428796928L);
  //  ll.add(190930284723347456L);
  //  ll.add(190930987462205440L);
  //  ll.add(190933831552638976L);
  //  ll.add(190935885020962816L);
  //  ll.add(197295811213471744L);
  //  ll.add(199813557787860992L);
  //  //
  //  return ll;
  //}

}
