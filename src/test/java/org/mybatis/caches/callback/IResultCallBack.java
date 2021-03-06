package org.mybatis.caches.callback;


/**
 * @author riverbo
 * @since 2018.01.26
 * 
 *   获取对象和主键的回调接口
 *
 */
public interface IResultCallBack<R> {
  
  /**
   * 组装数据
   *
   * @param r
   */
  R callback() throws Exception;
  
}
