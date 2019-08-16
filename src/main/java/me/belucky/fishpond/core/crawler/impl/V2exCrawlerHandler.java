/**
 * File Name: V2exCrawlerHandler.java
 * Date: 2019-08-16 08:45:54
 */
package me.belucky.fishpond.core.crawler.impl;

import me.belucky.fishpond.core.crawler.AbstractCrawlerHandler;

/**
 * Description: 爬取V2EX网站的最热新闻
 * @author shenzulun
 * @date 2019-08-16
 * @version 1.0
 */
public class V2exCrawlerHandler extends AbstractCrawlerHandler{
	
	public V2exCrawlerHandler(){
		super("crawler/v2ex-config.properties");
	}
}
