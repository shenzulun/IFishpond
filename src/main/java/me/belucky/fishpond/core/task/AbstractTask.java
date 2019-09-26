/**
 * File Name: AbstractTask.java
 * Date: 2019-06-13 10:14:46
 */
package me.belucky.fishpond.core.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.belucky.fishpond.util.CacheUtils;

/**
  *   功能说明：任务抽象处理类
 * @author shenzl
 * @date 2019-06-13
 * @version 1.0
 */
public abstract class AbstractTask implements ITask{
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	/**
	  *  缓存任务名称
	 */
	private String taskName;
	/**
	  *  配置文件的名称
	 */
	private String propName;
	
	public AbstractTask(String taskName, String propName) {
		super();
		this.taskName = taskName;
		this.propName = propName;
	}

	public void go() {
		long start = System.currentTimeMillis();
		log.info("开始执行任务：{}", taskName);
		execute();
		long end = System.currentTimeMillis();
		log.info("任务【{}】运行成功，耗时：{}ms", taskName, end - start);
	}
	
	/**
	 * 子类实现的方法
	 */
	public abstract void execute();

	public String getTaskName() {
		return taskName;
	}
	
	public String getPropName() {
		return propName;
	}
	
	public <T> T getObjectFromCacheInit(String key, T t) {
		T v = CacheUtils.getCache(key);
		if(v == null) {
			v = t;
		}
		return v;
	}
}
