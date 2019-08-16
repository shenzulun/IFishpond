/**
 * File Name: LaunchEntry.java
 * Date: 2019-08-15 16:11:14
 */
package me.belucky.fishpond;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import me.belucky.fishpond.controller.IndexController;


/**
 * Description: 应用启动入口
 * @author shenzulun
 * @date 2019-08-15
 * @version 1.0
 */
public class LaunchEntry extends JFinalConfig{
	protected static Logger log = LoggerFactory.getLogger(LaunchEntry.class);

	public void configConstant(Constants me) {
		me.setDevMode(true);
	}

	public void configRoute(Routes me) {
		me.add("/", IndexController.class);
	}

	public void configEngine(Engine me) {
		
	}

	public void configPlugin(Plugins me) {
		
	}

	public void configInterceptor(Interceptors me) {
		
	}

	public void configHandler(Handlers me) {
		
	}
	
	public void afterJFinalStart(){
		
	}
	
	public void beforeJFinalStop(){
		
	}
	
	public static void main(String[] args) {
		UndertowServer.start(LaunchEntry.class, 8888, true);
	}

}
