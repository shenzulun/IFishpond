/**
 * File Name: V2exCrawlerTask.java
 * Date: 2019-08-16 15:22:50
 */
package me.belucky.fishpond.core.task.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import me.belucky.fishpond.core.crawler.impl.V2exCrawlerHandler;
import me.belucky.fishpond.core.parser.IParser;
import me.belucky.fishpond.core.parser.ParserEnum;
import me.belucky.fishpond.core.parser.ParserFactory;
import me.belucky.fishpond.core.task.timer.SimpleTimerTask;

/**
 * Description: V2EX网站的爬虫任务
 * @author shenzulun
 * @date 2019-08-16
 * @version 1.0
 */
public class V2exCrawlerTask extends SimpleTimerTask{

	/**
	 * @param taskName
	 */
	public V2exCrawlerTask(String taskName) {
		super(taskName);
	}

	public void execute() {
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
