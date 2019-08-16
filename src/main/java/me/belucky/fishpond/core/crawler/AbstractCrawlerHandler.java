/**
 * File Name: AbstractCrawlerHandler.java
 * Date: 2019-08-15 16:48:50
 */
package me.belucky.fishpond.core.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import me.belucky.fishpond.util.CollectionUtils;
import me.belucky.fishpond.util.CommonUtils;
import me.belucky.fishpond.util.FileTools;
import me.belucky.fishpond.util.ParserUtils;

/**
 * Description: 爬虫程序默认处理类
 * @author shenzulun
 * @date 2019-08-15
 * @version 1.0
 */
public abstract class AbstractCrawlerHandler {
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * HTTP客户端
	 */
	private CloseableHttpClient httpClient = HttpClients.createDefault();
	/**
	 * 记录上次登录时间
	 */
	private AtomicLong cnt = new AtomicLong(0);
	/**
	 * 配置文件
	 */
	private Prop prop = null; 
	/**
	 * 文件保存路径
	 */
	private String fileSavePath = null;
	/**
	 * 缓存时间
	 */
	private String cacheTimePattern = null;
	/**
	 * 登录url
	 */
	private String logonUrl = null;
	/**
	 * 目标URL
	 * <pre>格式：url|GET|PAGE,以|分隔
	 * 例子：http://10.1.96.1:8080/operation/query?time={systime}&page={PAGE}&rows=100|GET|PAGE~total/100
	 * 第一位为URL
	 * 第二位为HTTP请求方式(默认为GET)
	 * 第三位为请求次数(默认为单次查询,如果是PAGE则会根据PAGE字段循环遍历,~后面的为返回报文中的总记录数字段,/后面的为每页记录数)	
	 * </pre>	 
	 */
	private String pullUrl = null;
	/**
	 * 用户名
	 */
	private String userName = null;
	/**
	 * 密码
	 */
	private String password = null;
	/**
	 * 参数map
	 */
	private Map<String, String> params = null;
	/**
	 * 配置文件名称
	 */
	private String propName;
	/**
	 * 登陆seesion保持时间
	 */
	private long sessionAliveTime = 1000 * 60 * 15;
	/**
	 * 代理IP	
	 */
	private String proxyHost;
	/**
	 * 代理PORT
	 */
	private int proxyPort;
	/**
	 * 默认的浏览器请求头
	 */
	public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36";
	
	public AbstractCrawlerHandler(){}
	
	public AbstractCrawlerHandler(String propName){
		this.propName = propName;
		if(params == null) {
			params = CollectionUtils.buildMap("systime", System.currentTimeMillis() + "");
		}
		init(params);
	}
	
	protected void init(Map<String, String> params){
		this.prop = PropKit.getProp(propName);
		this.fileSavePath = prop.get("file_save_path");
		this.cacheTimePattern = prop.get("cache_time");
		this.logonUrl = prop.get("logon_url");
		this.pullUrl = prop.get("pull_url");
		this.userName = prop.get("userName");
		this.password = prop.get("password");
		params.put("userName", userName);
		params.put("password", password);
		this.params = params;
		this.proxyHost = prop.get("proxy_host");
		this.proxyPort = prop.getInt("proxy_port", 0);
		String keepAliveStr = prop.get("session_alive_time");
		if(keepAliveStr != null && !"".equals(keepAliveStr)){
			sessionAliveTime = ParserUtils.stringToLong(keepAliveStr);
		}
	}
	
	/**
	 * 执行
	 */
	public List<String> handle(){
		logon();
		return getRecord();
	}
	
	/**
	 * 执行
	 * 支持分页查询所有
	 * @return
	 */
	public List<List<String>> handlePageable(){
		logon();
		return getJsonRecordPageable();
	}
	
	/**
	 * 模拟登陆
	 */
	private void logon(){
		//先判断登录URL是否为空
		if(logonUrl == null || "".equals(logonUrl)){
			return;
		}
		long old = cnt.get();
		if(old > 0 && (System.currentTimeMillis() - old) < sessionAliveTime ){
			//15分钟重新登陆
			log.info("有session,免登陆...");
			return;
		}	
		String url = ParserUtils.replaceString(logonUrl, params);
		log.debug(url);			
		HttpPost postMethod = new HttpPost(url);
		postMethod.setHeader("User-Agent", DEFAULT_USER_AGENT);
		if(proxyHost != null && !"".equals(proxyHost)){
			//代理
			HttpHost proxy = new HttpHost(proxyHost,proxyPort);
			RequestConfig defaultRequestConfig = RequestConfig.custom().setProxy(proxy).build();
			postMethod.setConfig(defaultRequestConfig);
		}
		try {
			//模拟登陆
			httpClient.execute(postMethod);		
			log.info("登陆成功...");
			cnt.set(System.currentTimeMillis());
		} catch (IOException e) {
			log.error("",e);
		}		
	}
	
	/**
	 * 获取记录
	 * @return
	 */
	private List<String> getRecord(){
		List<String> result = new ArrayList<String>();
		//读取配置文件中的url,设定爬取文件个数
		if(pullUrl == null || "".equals(pullUrl)){
			return result;
		}
		String[] pullUrlArr = pullUrl.split(",");
		for(int i=0,len=pullUrlArr.length;i<len;i++){
			boolean isReadFromCache = false;
			String filePath = getFilePath(i);
			File file = new File(filePath);
			if(file.exists()){
				//判断缓存保存时间
				long cacheTime = getCacheTime();
				if(cacheTime > 0){
					//判断文件修改日期
					long lastModified = file.lastModified();
					if(System.currentTimeMillis() - lastModified <= cacheTime){
						//还在缓存有效期内,读取本地文件
						isReadFromCache = true;
						log.info("从缓存文件中读取...");
						result.add(FileTools.getContent(filePath, true));
					}
				}
			}
			if(!isReadFromCache){
				log.info("开始爬取...");
				result.add(pull(pullUrlArr[i], filePath));
			}
		}
		
		return result;
	}
	
	/**
	 * 查询json返回结果
	 * @return
	 */
	private List<List<String>> getJsonRecordPageable(){
		List<List<String>> result = new ArrayList<List<String>>();
		//读取配置文件中的url,设定爬取文件个数
		if(pullUrl == null || "".equals(pullUrl)){
			return result;
		}
		String[] pullUrlArr = pullUrl.split(",");
		for(int i=0,len=pullUrlArr.length;i<len;i++){
			List<String> list = new ArrayList<String>();
			boolean isReadFromCache = false;
			params.put("PAGE", "1");
			String filePath = getFilePath(i);
			File file = new File(filePath);
			String output = null;
			if(file.exists()){
				//判断缓存保存时间
				long cacheTime = getCacheTime();
				if(cacheTime > 0){
					//判断文件修改日期
					long lastModified = file.lastModified();
					if(System.currentTimeMillis() - lastModified <= cacheTime){
						//还在缓存有效期内,读取本地文件
						isReadFromCache = true;
						log.info("从缓存文件中读取...");
						output = FileTools.getContent(filePath, true);
						list.add(output);
					}
				}
			}
			if(!isReadFromCache){
				log.info("开始爬取...");
				output = pull(pullUrlArr[i], filePath);
				list.add(output);
			}
			//判断是否有下一页
			String[] arr = pullUrlArr[i].split("\\|");
			if(arr.length > 2){
				String[] arr1 = arr[2].split("~");
				if("PAGE".equals(arr1[0])){
					//有分页标志
					String[] arr2 = arr1[1].split("/");
					String totalStr = CommonUtils.regexMatch(output, "\"" + arr2[0] + "\":(.+?),");
					int total = Integer.valueOf(totalStr);
					int pageSize = Integer.valueOf(arr2[1]);
					if(total > pageSize){
						//存在多页
						int pages = (total % pageSize == 0) ? total / pageSize : (total / pageSize) + 1;
						for(int j=2;j<=pages;j++){
							params.put("PAGE", String.valueOf(j));
							String filePath1 = getFilePath(i);
							if(isReadFromCache){
								output = FileTools.getContent(filePath1, true);
								list.add(output);
							}else{
								output = pull(pullUrlArr[i], filePath1);
								list.add(output);
							}
						}
					}
				}
			}
			result.add(list);
		}
		
		return result;
	}
	
	/**
	 * 爬取请求
	 */
	private synchronized String pull(String pullUrl, String filePath){
		long start = System.currentTimeMillis();
		String result = null;
		String url = ParserUtils.replaceString(pullUrl, params);
		//判断请求类型
        HttpRequestBase httpMethod = null;
		String[] arr = url.split("\\|");
		if(arr.length == 1){
			httpMethod = new HttpGet(url);
		}else{
			url = arr[0];
			String method = arr[1].toUpperCase();
			if("POST".equals(method)){
				httpMethod = new HttpPost(url);
			}else if("GET".equals(method)){
				httpMethod = new HttpGet(url);
			}else if("PUT".equals(method)){
				httpMethod = new HttpPut(url);
			}else if("DELETE".equals(method)){
				httpMethod = new HttpDelete(url);
			}else{
				//默认post
				httpMethod = new HttpPost(url);
			}
		}
//		httpMethod.setConfig(defaultRequestConfig);
		httpMethod.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
		if(proxyHost != null && !"".equals(proxyHost)){
			//代理
			HttpHost proxy = new HttpHost(proxyHost,proxyPort);
			RequestConfig defaultRequestConfig = RequestConfig.custom().setProxy(proxy).build();
			httpMethod.setConfig(defaultRequestConfig);
		}
		try {
			CloseableHttpResponse response = httpClient.execute(httpMethod);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK){
				log.info("请求成功...");
				long end = System.currentTimeMillis();
				String cost = "返回耗时: " + (end - start);
				log.info(cost);
				
				BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuffer buff = new StringBuffer();
				String line = null;
				String lineSeparator = System.getProperty("line.separator","\n");
				while((line=br.readLine()) != null){
					buff.append(line).append(lineSeparator);
				}
				result = buff.toString(); 
				FileTools.exportFile(filePath, result);
			}else{
				log.info(statusCode + "");
				log.info("请求失败...");
			}
		} catch (Exception e) {
			log.error("",e);
		} finally{
			// 释放连接
			httpMethod.releaseConnection();
		}
		return result;	
	}
	
	/**
	 * 获取文件路径
	 * @return
	 */
	public String getFilePath(int i){
		return ParserUtils.replaceString(fileSavePath.split(",")[i], params);
	}
	
	/**
	 * 获取缓存时间
	 * @return
	 */
	public Long getCacheTime(){
		Long cacheTime = 0L;
		if(cacheTimePattern != null && !"".equals(cacheTimePattern)){
			cacheTime =  ParserUtils.stringToLong(cacheTimePattern);
		}
		return cacheTime;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}
}
