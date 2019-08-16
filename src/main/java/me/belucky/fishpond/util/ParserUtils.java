/**
 * File Name: ParserUtils.java
 * Date: 2019-08-16 11:12:10
 */
package me.belucky.fishpond.util;

import java.util.Map;

import me.belucky.fishpond.core.parser.IParser;
import me.belucky.fishpond.core.parser.ParserEnum;
import me.belucky.fishpond.core.parser.ParserFactory;

/**
 * Description: 解析工具类
 * @author shenzulun
 * @date 2019-08-16
 * @version 1.0
 */
public class ParserUtils {
		
	/**
	 * 字符串替换
	 * input: hello ${name}
	 * map: name: world
	 * output: hello world
	 * @param input
	 * @param mapCond
	 * @return
	 */
	public static String replaceString(String input, Map<String, String> mapCond) {
		IParser<String> parser =  ParserFactory.getParser(ParserEnum.REPLACE_STRING);
		parser.setMapCond(mapCond);
		return parser.parse(input);
	}
	
	/**
	 * 将字符串解析成数值
	 * ${1d12h30m15s}
	 * =>
	 * 24*60*60*1000+12*60*60*1000+30*60*1000+15*1000
	 * @param input
	 * @return
	 */
	public static Long stringToLong(String input) {
		IParser<Long> parser =  ParserFactory.getParser(ParserEnum.NUMBER);
		return parser.parse(input);
	}

}
