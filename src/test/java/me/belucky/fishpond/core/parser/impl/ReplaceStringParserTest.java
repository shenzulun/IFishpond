/**
 * File Name: ReplaceStringParserTest.java
 * Date: 2019-08-16 08:32:32
 */
package me.belucky.fishpond.core.parser.impl;

import org.junit.Test;
import me.belucky.fishpond.core.parser.IParser;
import me.belucky.fishpond.core.parser.ParserEnum;
import me.belucky.fishpond.core.parser.ParserFactory;
import me.belucky.fishpond.util.CollectionUtils;

/**
 * Description: 
 * @author shenzulun
 * @date 2019-08-16
 * @version 1.0
 */
public class ReplaceStringParserTest {
	IParser<String> parser =  ParserFactory.getParser(ParserEnum.REPLACE_STRING);
	
	@Test
	public void test1() {
		String input = "张三${zz}asdhjkhkjzz行行号123";
		parser.setMapCond(CollectionUtils.buildMap("zz", "hello"));
		String output = parser.parse(input);
		System.out.println(output);
	}
}
