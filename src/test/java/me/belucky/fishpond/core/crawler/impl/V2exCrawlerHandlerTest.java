/**
 * File Name: V2exCrawlerHandlerTest.java
 * Date: 2019-08-16 08:53:12
 */
package me.belucky.fishpond.core.crawler.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import com.jfinal.kit.PropKit;
import me.belucky.fishpond.core.parser.ParserEnum;
import me.belucky.fishpond.core.parser.ParserFactory;
import me.belucky.fishpond.core.parser.IParser;

/**
 * Description: 
 * @author shenzulun
 * @date 2019-08-16
 * @version 1.0
 */
public class V2exCrawlerHandlerTest {
	
	@Test
	public void test1() {
		PropKit.use("crawler/v2ex-config.properties");
		V2exCrawlerHandler test = new V2exCrawlerHandler();
		List<String> result = test.handle();
		System.out.println(result.size());
		
		IParser<Map<String, List<String>>> parser = ParserFactory.getParser(ParserEnum.HTML);
		String pattern = "<span class=\"item_title\"><a href=\"(.+?)</a></span>";
		parser.setListCond(Arrays.asList(new String[]{pattern}));
		Map<String, List<String>> map = parser.parse(result.get(0));
		List<String> list = map.get(pattern);
		for(String row : list) {
			String[] arr = row.split("\">");
			System.out.print(arr[1] + ": ");
			System.out.println("https://www.v2ex.com" + arr[0]);
		}
	}

}
