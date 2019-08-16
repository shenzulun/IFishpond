/**
 * File Name: IndexController.java
 * Date: 2019-08-15 16:31:18
 */
package me.belucky.fishpond.controller;

import me.belucky.fishpond.core.task.TaskInitCenter;

/**
 * Description: 
 * @author shenzulun
 * @date 2019-08-15
 * @version 1.0
 */
public class IndexController extends BaseController{
	
	public Class<?> setObj() {
		return null;
	}

	public void go(Object dto, String methodName) {
	}
	
	public void index() {
		renderText("Hello JFinal World.");
	}
	
	public void admin(){
		setAttr("tasks",TaskInitCenter.getTaskList());
		render("admin/admin.html");
	}

}
