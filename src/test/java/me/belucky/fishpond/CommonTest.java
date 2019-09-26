/**
 * File Name: CommonTest.java
 * Date: 2019-08-16 16:44:42
 */
package me.belucky.fishpond;

import java.lang.reflect.Constructor;

import org.junit.Test;

import me.belucky.fishpond.core.task.timer.SimpleTimerTask;

/**
 * Description: 
 * @author shenzulun
 * @date 2019-08-16
 * @version 1.0
 */
public class CommonTest {
	
	@Test
	public void test1() throws Exception {
		Class<?> cls = Class.forName("me.belucky.fishpond.core.task.impl.V2exCrawlerTask");
		Constructor<?> constructor = cls.getConstructor(String.class);
		SimpleTimerTask simpleTask = (SimpleTimerTask) constructor.newInstance("123");
	}

}
