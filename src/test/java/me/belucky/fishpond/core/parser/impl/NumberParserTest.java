/**
 * File Name: NumberParserTest.java
 * Date: 2019-08-15 17:24:33
 */
package me.belucky.fishpond.core.parser.impl;

import org.junit.Assert;
import org.junit.Test;
import me.belucky.fishpond.core.parser.IParser;
import me.belucky.fishpond.core.parser.ParserEnum;
import me.belucky.fishpond.core.parser.ParserFactory;

/**
 * Description: 
 * @author shenzulun
 * @date 2019-08-15
 * @version 1.0
 */
public class NumberParserTest {
	IParser<Long> parser =  ParserFactory.getParser(ParserEnum.NUMBER);
	
	@Test
	public void test1() {
		long v = parser.parse("${1d12h30m15s}");
		System.out.println(v);	
		
		long expect = 24*60*60*1000+12*60*60*1000+30*60*1000+15*1000;
		System.out.println(expect);
		Assert.assertEquals(expect, v);
	}
	
	@Test
	public void test2() {
		long v = parser.parse("{1w1d12h30m15s}");
		System.out.println(v);	
		
		long expect = 7*24*60*60*1000+24*60*60*1000+12*60*60*1000+30*60*1000+15*1000;
		System.out.println(expect);
		Assert.assertNotEquals(expect, v);
	}

}
