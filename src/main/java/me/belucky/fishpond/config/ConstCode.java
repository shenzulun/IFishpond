/**
 * File Name: ConstCode.java
 * Date: 2019-08-16 15:36:22
 */
package me.belucky.fishpond.config;

/**
 * Description: 常量类
 * @author shenzulun
 * @date 2019-08-16
 * @version 1.0
 */
public final class ConstCode {
	/**
 	 * 空白字符串
 	 */
 	public static final String BLANK_STRING = "";
 	/**
 	 * 换行符
 	 */
 	public static final String LINE_BREAK = System.getProperty("line.separator","\n");
 	/**
 	 * 行内分隔符  |
 	 */
 	public static final String LINE_SEPARATOR = "|";
 	/**
 	 * 任务类型-定时任务
 	 */
 	public static final int TASK_TYPE_TIME = 1;
 	/**
 	 * 任务类型-一次性任务
 	 */
 	public static final int TASK_TYPE_ONCE = 2;
 	/**
 	 * 默认的文件字符集
 	 */
 	public static final String DEFAULT_FILE_ENCODE = "UTF-8";
 	
}
