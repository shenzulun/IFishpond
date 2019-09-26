/**
 * File Name: TaskInitCenter.java
 * Date: 2017-5-25 下午04:59:37
 */
package me.belucky.fishpond.core.task;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import me.belucky.fishpond.config.ConstCode;
import me.belucky.fishpond.core.task.dto.TaskDefinitionDTO;
import me.belucky.fishpond.core.task.timer.SimpleTimerTask;
import me.belucky.fishpond.core.task.timer.SimpleTimerTaskFactory;
import me.belucky.fishpond.util.DateTimeUtils;
import me.belucky.fishpond.util.ParserUtils;


/**
 * 功能说明: 任务初始化中心
 * @author shenzl
 * @date 2017-5-25
 * @version 1.0
 */
public final class TaskInitCenter {
	protected static Logger log = LoggerFactory.getLogger(TaskInitCenter.class);
	/**
	  * 任务初始化标志
	 */
	private static volatile boolean taskInitFlag = false;         
	/**
	 * 定时任务清单
	 */
	private static List<TaskDefinitionDTO> taskList = new ArrayList<TaskDefinitionDTO>();
	/**
	 * 定时任务MAP
	 */
	private static Map<String, TaskDefinitionDTO> taskMap = new ConcurrentHashMap<String, TaskDefinitionDTO>();
	
	/**
	 * 任务中心初始化
	 */
	public static void go(){	
		if(!taskInitFlag){
			//只能初始化一次
			taskInitFlag = true;
			taskInit();
		}
		try{
			for(TaskDefinitionDTO taskDTO : taskList){
				Class<?> cls = Class.forName(taskDTO.getTaskClassPath());
				int taskType = taskDTO.getTaskType();
				if(taskType == ConstCode.TASK_TYPE_ONCE) {
					//一次性任务
					Constructor<?> constructor = cls.getConstructor(String.class, String.class);
					ITask onceTask = (ITask) constructor.newInstance(taskDTO.getTaskName(), taskDTO.getPropPath());
					onceTask.go();
				}else if(taskType == ConstCode.TASK_TYPE_TIME) {
					//定时任务
					Constructor<?> constructor = cls.getConstructor(String.class);
					SimpleTimerTask simpleTask = (SimpleTimerTask) constructor.newInstance(taskDTO.getTaskName());
					simpleTask.setTaskId(taskDTO.getTaskId());
					timingTask(taskDTO, simpleTask);
				}
			}
		}catch(Exception e){
			log.error("任务批量初始化失败",e);
		}
	}
	
	/**
	 * 定时任务
	 * @param propName
	 * @param cls
	 * @param taskName
	 */
	private static <T extends SimpleTimerTask> void timingTask(TaskDefinitionDTO taskDTO, T task){
		Prop prop = PropKit.getProp(taskDTO.getPropPath());
		String interval = prop.get("ud-interval"); 
		String startTime = prop.get("ud-starttime");	//当天运行日期
		String customStart = prop.get("ud-custom-start");	//个性化运行日期
		String oldTaskName = task.getTaskName();
		if(customStart != null){
			String[] customStartArr = customStart.split(",");
			int cnt = 0;
			StringBuffer buff = new StringBuffer();
			for(String st : customStartArr){
				Date expectDate = ParserUtils.stringToDate(st);
				task.setTaskName(oldTaskName + ++cnt);
				SimpleTimerTaskFactory.schedule(task, DateTimeUtils.getDelay(expectDate), ParserUtils.stringToLong(interval),DateTimeUtils.getDateTime(expectDate));
				if(cnt != 1){
					buff.append(" | ");
				}
				buff.append(DateTimeUtils.getDateTime(expectDate));
			}
			taskDTO.setFirstExpectStart(buff.toString());
			
		}else{
			long delay = DateTimeUtils.getDelay(startTime);
			SimpleTimerTaskFactory.schedule(task, delay, ParserUtils.stringToLong(interval),startTime);
			taskDTO.setFirstExpectStart(DateTimeUtils.getDate(new Date()) + " " + startTime);
		}
		taskDTO.setIntervalExpr(interval);
		taskMap.put(taskDTO.getTaskId(), taskDTO);
	}
	
	/**
	 * 解析定时任务的配置
	 */
	public static void taskInit(){
		SAXReader saxReader = new SAXReader();
		InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream("task-init-config.xml");
		Document document;
		try {
			document = saxReader.read(ins);
			Element root = document.getRootElement();		
			String defaultClasspath = root.element("default-classpath").getTextTrim();
			List<Element> childList = root.elements("task");		
			for(Element e : childList){
				String id = e.attributeValue("id");
				String classpath = defaultClasspath;
				String classpathEle = e.attributeValue("classpath");
				if(classpathEle != null){
					classpath = classpathEle;
				}
				String prop = e.attributeValue("prop");
				String title = e.attributeValue("title");
				String className = e.getTextTrim();
				TaskDefinitionDTO taskDTO = new TaskDefinitionDTO(id,title,classpath + "." + className,prop);
				String taskType = e.attributeValue("type");
				if(taskType == null) {
					taskType = "2";
				}
				taskDTO.setTaskType(Integer.valueOf(taskType));
				taskList.add(taskDTO);
				taskMap.put(id, taskDTO);
			}
		} catch (DocumentException e) {
			log.error("task-init-config.xml解析出错", e);		
		}
	}
	
	/**
	 * 运行指定任务
	 * @param taskId
	 */
	public static void runTask(String taskId){
		TaskDefinitionDTO taskDTO = taskMap.get(taskId);
		Class<?> cls;
		try {
			cls = Class.forName(taskDTO.getTaskClassPath());
			int taskType = taskDTO.getTaskType();
			if(taskType == ConstCode.TASK_TYPE_ONCE) {
				//一次性任务
				Constructor<?> constructor = cls.getConstructor(String.class, String.class);
				ITask onceTask = (ITask) constructor.newInstance(taskDTO.getTaskName(), taskDTO.getPropPath());
				onceTask.go();
			}else if(taskType == ConstCode.TASK_TYPE_TIME) {
				Constructor<?> constructor = cls.getConstructor(String.class);
				SimpleTimerTask simpleTask = (SimpleTimerTask) constructor.newInstance(taskDTO.getTaskName());
				simpleTask.setTaskId(taskId);
				//不能thread方式运行,不然取不到日志
				simpleTask.run();   
			}
		} catch (Exception e) {
			log.error("定时任务单个初始化失败",e);
		}
	}

	public static List<TaskDefinitionDTO> getTaskList() {
		return taskList;
	}

	public static Map<String, TaskDefinitionDTO> getTaskMap() {
		return taskMap;
	}
	
	/**
	 * 刷新任务状态
	 * @param taskId
	 */
	public synchronized static void refreshTaskStatus(String taskId){
		if(taskId == null || "".equals(taskId)){
			return;
		}
		for(TaskDefinitionDTO taskDTO : taskList){
			if(taskId.equals(taskDTO.getTaskId())){
				taskDTO.setLastRunDateStr(DateTimeUtils.getDateTimeNow());
				break;
			}
		}
		TaskDefinitionDTO taskDTO = taskMap.get(taskId);
		if(taskDTO != null){
			taskDTO.setLastRunDateStr(DateTimeUtils.getDateTimeNow());
		}
	}
	
}
