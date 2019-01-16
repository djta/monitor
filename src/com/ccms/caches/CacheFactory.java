package com.ccms.caches;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.CacheManagerBuilder;
import org.ehcache.Cache.Entry;
import org.ehcache.config.CacheConfigurationBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


 /**
  * 保证程序中只有一个缓存对象,多次调用共享一个内存地址
 */
public final class CacheFactory {
	
	private static final Log log = LogFactory.getLog(CacheFactory.class);
	
	private static Cache cache;
	
	/**
	 * 初始化缓存对象
	 */
	private CacheFactory(){
		super();
		CacheManager manager = CacheManagerBuilder.newCacheManagerBuilder().withCache(CacheConst.CACHE_NAME,
		        CacheConfigurationBuilder.newCacheConfigurationBuilder()
	            .buildConfig(Object.class, Object.class)) 
	            .build(false);
		manager.init();
		cache = manager.getCache(CacheConst.CACHE_NAME, Object.class, Object.class);
	}
	
	/**
	 * 获取缓存对象
	 * @return
	 */
	public static CacheFactory getInstance(){
		return CacheFactoryHolder.factory;
	}
	
	/**
	 * 往缓存中添加值
	 * @param key
	 * @param value
	 */
	public <T> void addCache(String key,T value){
		log.debug("添加缓存：[" + key + "]" );
		cache.put(key, value);
	}
	
	/**
	 * 根据键值删除缓存
	 * @param key
	 */
	public void remove(String key){
		cache.remove(key);
	}
	
	/**
	 * 缓存替换
	 * @param key
	 * @param oldVal
	 * @param newVal
	 */
	public  <T> void replace(String key,T oldVal,T newVal){
		cache.replace(key, oldVal, newVal);
	}
	
	/**
	 * 删除所有的缓存
	 */
	public void removeAll(){
		long begin = System.currentTimeMillis();
		log.debug("开始删除缓存");
		cache.clear();
		log.debug("删除缓存结束，用时：[" + ((System.currentTimeMillis() - begin) / 1000f) + "] ms");
	}
	
	/**
	 * 获取缓存中的数据
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(String key){
		Object el = (Object) cache.get(key);
		if(el != null){
			return (T)el;
		}
		return null;
		
	}
	
	private static class CacheFactoryHolder{
		private static CacheFactory factory = new CacheFactory();
	}

	public static Cache getCache() {
		return cache;
	}

	public static void setCache(Cache cache) {
		CacheFactory.cache = cache;
	}
	
}
