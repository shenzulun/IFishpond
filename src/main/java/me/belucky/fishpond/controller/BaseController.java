/**
 * File Name: BaseController.java
 * Date: 2019-08-15 16:39:42
 */
package me.belucky.fishpond.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

import me.belucky.fishpond.core.tail.Tail;
import me.belucky.fishpond.dto.MessageDTO;

/**
 * Description: 
 * @author shenzulun
 * @date 2019-08-15
 * @version 1.0
 */
public abstract class BaseController extends Controller{
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	 public abstract Class<?> setObj();
		
	 public abstract void go(Object dto, String methodName);
	
	 private String remoteAddr;
	
	 private MessageDTO retDto = null;
	
	 public void autorun(){
		 String methodName = this.getPara("methodName");
		 log.debug(methodName);
		 this.remoteAddr = super.getRequest().getRemoteAddr();
		 log.debug("当前访问IP: {}", remoteAddr == null ? "" : remoteAddr);
		 Object dto = getBean(setObj());
		 //日志监视
		 StringBuffer buff = new StringBuffer();
		 Tail tail = new Tail(PropKit.getProp("log4j.properties").get("log4j.appender.file.File"),buff);
		 go(dto, methodName);
		 if(retDto == null){
			 retDto = new MessageDTO();
			 retDto.setRetMsg(tail.getBuffer().toString());
		 }
		 renderJson(retDto);
	 }
	
	 public void invoke(Object target, String methodName, Object dto) {
		 try {
			 Method m = target.getClass().getDeclaredMethod(methodName, dto.getClass());
			 m.invoke(target, dto);
		 } catch (SecurityException e) {
			 log.error("",e);
		 } catch (NoSuchMethodException e) {
			 log.error("",e);
		 } catch (IllegalAccessException e) {
			 log.error("",e);
		 } catch (IllegalArgumentException e) {
			 log.error("",e);
		 } catch (InvocationTargetException e) {
			 log.error("",e);
		 }
	 }

	 public String getRemoteAddr() {
		 return remoteAddr;
	 }

	 public void setRemoteAddr(String remoteAddr) {
		 this.remoteAddr = remoteAddr;
	 }

	 public MessageDTO getRetDto() {
		 return retDto;
	 }

	 public void setRetDto(MessageDTO retDto) {
		 this.retDto = retDto;
	 }
	
}
