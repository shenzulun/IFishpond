/**
 * File Name: LogAuditHandler.java
 * Date: 2019-08-16 15:13:40
 */
package me.belucky.fishpond.core;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.handler.Handler;

/**
 * Description: 日志记录
 * @author shenzulun
 * @date 2019-08-16
 * @version 1.0
 */
public class LogAuditHandler extends Handler{
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, boolean[] isHandled) {
		if(target != null && !target.startsWith("/static")){
			log.info(target);
			Enumeration<String> params = request.getParameterNames();
			while(params.hasMoreElements()){
				String param = params.nextElement();
				log.info("参数名: {}, 参数值: {}", param, request.getParameter(param));
			}
			String remoteAddr = request.getRemoteAddr();
			log.info("当前访问IP: {}", remoteAddr == null ? "" : remoteAddr);
		}
		super.next.handle(target, request, response, isHandled);
	}

}