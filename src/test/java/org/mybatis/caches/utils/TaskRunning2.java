package org.mybatis.caches.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.mybatis.caches.callback.IResultCallBack;
import org.mybatis.caches.redis.RedisCaches;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时任务，运行统一类
 * 
 * @author riverbo
 * @since 2018.07.06
 */
public class TaskRunning2 {
  
  private static final Logger log = LoggerFactory.getLogger(TaskRunning2.class);

  private static final String DEFAULT_ID = "z-jss-task2-REDIS";

  private static RedisCaches cache = new RedisCaches(DEFAULT_ID);

//  public static void newCache() {
//  }

  public static void clear() {
    cache.clear();
  }

  public static <T> T lockOnly(String taskId, String taskName, IResultCallBack<T> cb, int ms) throws Exception {
    return lockUnlock(taskId, taskName, cb, ms, false);
  }
  
  public static <T> T lockUnlock(String taskId, String taskName, IResultCallBack<T> cb, int ms) throws Exception {
    return lockUnlock(taskId, taskName, cb, ms, true);
  }
  /**
   * lock
   * 
   * @param taskId
   * @param taskName
   * @param cb
   * @param tm
   * @throws Exception
   * @author root
   * @since 2018.07.06
   */
  public static <T> T lockUnlock(String taskId, String taskName, IResultCallBack<T> cb, int ms, boolean undo)
      throws Exception {
    boolean lock = cache.lock(taskId, true);
    T t = null;
    Date start = new Date();
    try {
      if (lock) {
        if (cb != null) {
          t = cb.callback();
        }
      }
    } catch (Exception e) {
      log.error("doIt: task[{}], {}", taskName, e.getMessage());
      throw e;
    } finally {
      Date end = null;
      if (log.isInfoEnabled()) {
        end = new Date();
      }
      Boolean unlock = null;
      if (lock) {
        Thread.sleep(ms);
        if (undo) {
          unlock = cache.unlock(taskId);
        }
      }
      if (log.isInfoEnabled()) {
        //if (lock) {
          String lockStr = null;
          if (lock) {
            lockStr = "ok";
          } else {
            lockStr = "skip";
          }
          //log.info("doIt: task[{}] {}, lock({}), unlock({}), end.",
          //    taskName, lockStr, lock, unlock);
        //}
        log.info("doIt: task[{}] {}, lock({}), unlock({})\n\t start:{}\n\t stop-:{}\n\t span-:{}, applies({}), end.",
            taskName, lockStr, lock, unlock, toDateTime(start), toDateTime(end),
            getTimes(start, end), t);
      }
    }
    return t;
  }

//  private static <T> T lockUnlock(String taskId, String taskName, IResultCallBack<T> cb, int ms, boolean unlock) throws Exception {
//    T t = null;
//    String orgKey = taskId;
//    StringBuilder sb = new StringBuilder();
//    try {
//      Boolean b = cache.lock(orgKey, 1);
//      sb.append(taskName + ": " + orgKey + ", " + "lock( " + b + "), ");
//      if (b) {
//        if (cb != null) {
//          t = cb.callback();
//        }
//        Thread.sleep(ms);
//        if (unlock) {
//          boolean un = cache.unlock(orgKey);
//          sb.append("unlock: " + un);
//        } else {
//          sb.append("unlock: " + "don't");          
//        }
//      } else {
//        sb.append("unlock: none");
//      }
//      if (b) {
//        log.debug("lockUnlock: {}", sb.toString());
//      }
//      Thread.sleep(0);
//    } catch (Exception e) {
//    } finally {
//      sb.setLength(0);
//      sb = null;
//    }
//    return t;
//  }

  /**
   * 两个时间相差距离多少天多少小时多少分多少秒
   * 
   * @param str1 时间参数 1 格式：1990-01-01 12:00:00
   * @param str2 时间参数 2 格式：2009-01-01 12:00:00
   * @return long[] 返回值为：{天, 时, 分, 秒}
   */
  private static long[] getBewteenTimes(Date one, Date two) {
    long day = 0;
    long hour = 0;
    long min = 0;
    long sec = 0;
    long ms = 0;
    //
    long time1 = one.getTime();
    long time2 = two.getTime();
    long diff;
    if (time1 < time2) {
      diff = time2 - time1;
    } else {
      diff = time1 - time2;
    }
    day = diff / (24 * 60 * 60 * 1000);
    hour = (diff / (60 * 60 * 1000) - day * 24);
    min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
    sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
    ms = (diff - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - sec * 1000);
    long[] times = { day, hour, min, sec, ms };
    return times;
  }

  public static String getTimes(Date one, Date two) {
    return getTimes(one, two, "");
  }

  /**
   * 两个时间相差距离多少天多少小时多少分多少秒
   * 
   * @param str1 时间参数 1 格式：1990-01-01 12:00:00
   * @param str2 时间参数 2 格式：2009-01-01 12:00:00
   * @return String 返回值为：{天, 时, 分, 秒}
   */
  private static String getTimes(Date one, Date two, String split) {
    long[] times = getBewteenTimes(one, two);
    String[] units = { "d", "h", "m", "s", "ms" };
    StringBuilder sb = new StringBuilder();
    try {
      for (int i = 0; i < times.length; i++) {
        long l = times[i];
        if (l != 0 || i == (times.length - 1)) {
          if (sb.length() > 0) {
            sb.append(", ");
          }
          sb.append(l);
          sb.append(split);
          sb.append(units[i]);
        }
      }
      return sb.toString();
    } finally {
      sb.setLength(0);
      sb = null;
    }
  }

  /**
   * toDateTime
   * 
   * @param date
   * @return
   * @throws Exception
   * @author riverbo
   * @since 2018.07.05
   */
  //  private static String toDateTime(Date date) throws Exception {
  //    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date);
  //  }

  /**
   * @param date
   * @return
   * @throws Exception
   * @author riverbo
   * @since 2018.07.05
   */
  public static String toDateTime(Date date) throws Exception {
    return new SimpleDateFormat("HH:mm:ss.SSS").format(date);
  }

}
