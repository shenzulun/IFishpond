/**
 * File Name: CommonUtils.java
 * Date: 2019-08-16 08:40:54
 */
package me.belucky.fishpond.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 通用工具类
 * @author shenzulun
 * @date 2019-08-16
 * @version 1.0
 */
public class CommonUtils {

	/**
	 * 正则匹配
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static String regexMatch(String source, String pattern){
		Pattern p1 = Pattern.compile(pattern);	
		return regexMatch(source, p1);
	}
	/**
	 * 正则匹配
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static String regexMatch(String source, Pattern pattern){
		String result = null;
		Matcher matcher = pattern.matcher(source);
		while(matcher.find()){
			result = matcher.group(1);
			break;
		}
		return result;
	}
}
