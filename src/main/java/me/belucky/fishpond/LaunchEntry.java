/**
 * File Name: LaunchEntry.java
 * Date: 2019-08-15 16:11:14
 */
package me.belucky.fishpond;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import me.belucky.fishpond.controller.IndexController;
import me.belucky.fishpond.core.LogAuditHandler;
import me.belucky.fishpond.util.CommonUtils;


/**
 * Description: 应用启动入口
 * @author shenzulun
 * @date 2019-08-15
 * @version 1.0
 */
public class LaunchEntry extends JFinalConfig{
	protected static Logger log = LoggerFactory.getLogger(LaunchEntry.class);

	public void configConstant(Constants me) {
		PropKit.use("sys-config.properties");
		CommonUtils.initPropIgnore("", "sys-config.properties");
		CommonUtils.initProp("crawler");
		me.setDevMode(PropKit.getBoolean("devMode", true));
		me.setMaxPostSize(1024 * 1024 * 1024);    //1G
		me.setViewType(ViewType.FREE_MARKER);
	}

	public void configRoute(Routes me) {
		me.setBaseViewPath("/WEB-INF/content");
		me.add("/", IndexController.class,"/");
	}

	public void configEngine(Engine me) {
		
	}

	public void configPlugin(Plugins me) {
//		String url = PropKit.get("jdbc-url");
//		log.info(url);
//		String driverClass = PropKit.get("jdbc-driverClass");
//		String username = PropKit.get("jdbc-user");
//		String password = PropKit.get("jdbc-password");
//		DruidPlugin druid = new DruidPlugin(url, username, password,driverClass); 
//		me.add(druid);
//		ActiveRecordPlugin arp = new ActiveRecordPlugin(druid);
//		arp.setShowSql(PropKit.getBoolean("showSql", false));
//		me.add(arp);
	}

	public void configInterceptor(Interceptors me) {
		
	}

	public void configHandler(Handlers me) {
		me.add(new LogAuditHandler());
		me.add(new ContextPathHandler("contextPath"));
	}
	
	public void afterJFinalStart(){
		CommonUtils.initCache();
		try {
			String indexUrl = PropKit.get("index_url");
			if(indexUrl != null && !"".equals(indexUrl)){
				Desktop.getDesktop().browse(new URI(indexUrl));
			}
		} catch (IOException e) {
			log.error("打开浏览器失败...",e);
		} catch (URISyntaxException e) {
			log.error("打开浏览器失败...",e);
		}
	}
	
	public void beforeJFinalStop(){
		
	}
	
	public static void main(String[] args) {
		UndertowServer.start(LaunchEntry.class, 8888, true);
	}

}
