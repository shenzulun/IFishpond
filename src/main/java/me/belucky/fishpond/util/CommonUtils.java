/**
 * File Name: CommonUtils.java
 * Date: 2019-08-16 08:40:54
 */
package me.belucky.fishpond.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.kit.PropKit;

import me.belucky.fishpond.core.task.TaskInitCenter;


/**
 * Description: 通用工具类
 * @author shenzulun
 * @date 2019-08-16
 * @version 1.0
 */
public class CommonUtils {
	protected static Logger log = LoggerFactory.getLogger(CommonUtils.class);
	
	/**
	 * 初始化缓存
	 */
	public static void initCache(){
		//先清除所有缓存
		CacheUtils.clearCache();
		log.info("缓存清除成功");
		TaskInitCenter.go();
	}

	/**
	 * 正则匹配
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static String regexMatch(String source, String pattern){
		Pattern p1 = Pattern.compile(pattern);	
		return regexMatch(source, p1);
	}
	/**
	 * 正则匹配
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static String regexMatch(String source, Pattern pattern){
		String result = null;
		Matcher matcher = pattern.matcher(source);
		while(matcher.find()){
			result = matcher.group(1);
			break;
		}
		return result;
	}
	
	/**
	 * Java bean  get 反射
	 * @param <T>
	 * @param target
	 * @param fieldName
	 * @return
	 */
	public static <T> T invoke(Object target, String fieldName) {
		return invoke(target, fieldName, "get", null);
	}
	
	/**
	 * Java bean 反射
	 * @param <T>
	 * @param target
	 * @param fieldName
	 * @param methodPrefix
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invoke(Object target, String fieldName, String methodPrefix, T newValue) {
		String methodName = methodPrefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		T t = null;
		try {
			Class<?> cls = target.getClass();
			if("set".equals(methodPrefix)){
				//set方法  先获取该字段的属性
				Field field = cls.getDeclaredField(fieldName);
				Method m = target.getClass().getDeclaredMethod(methodName, field.getType());
				Object v = m.invoke(target, newValue);
				t = (T)v;
			}else if("get".equals(methodPrefix)){
				Method m = target.getClass().getDeclaredMethod(methodName);
				Object v = m.invoke(target);
				t = (T)v;
			}
		} catch (SecurityException e) {
			log.error("",e);
		} catch (NoSuchMethodException e) {
			log.error("",e);
		} catch (IllegalArgumentException e) {
			log.error("",e);
		} catch (IllegalAccessException e) {
			log.error("",e);
		} catch (InvocationTargetException e) {
			log.error("",e);
		} catch (NoSuchFieldException e) {
			log.error("",e);
		}
		return t;
	}
	
	/**
	 * 判断是否存在中文字符
	 * @param input
	 * @return
	 */
	public static boolean isExistChineseChar(String input){
		boolean isExist = false;
		char[] arr = input.toCharArray();
		for(char c : arr){
			if(c >= '\u4e00' && c <= '\u9fa5'){
				return true;
			}else if(c >= '\uf900' && c <= '\ufa2d'){
				return true;
			}
		}
		return isExist;
	}
	
	/**
	 * 判断是否存在英文字母
	 * @param input
	 * @return
	 */
	public static boolean isExistAlphabetChar(String input){
		boolean isExist = false;
		char[] arr = input.toCharArray();
		for(char c : arr){
			if(c >= 'a' && c <= 'z'){
				return true;
			}else if(c >= 'A' && c <= 'Z'){
				return true;
			}
		}
		return isExist;
	}
	
	/**
	 * 是否非空
	 * @param input
	 * @return
	 */
	public static boolean isNotNull(String input){
		if(input == null || "".equals(input)){
			return false;
		}
		return true;
	}
	
	/**
	 * 是否非空白
	 * @param input
	 * @return
	 */
	public static boolean isNotBlank(String input){
		if(input == null || "".equals(input.trim())){
			return false;
		}
		return true;
	}
	
	/**
	 * 比较两个字符串是否相同
	 * @param source
	 * @param target
	 * @return
	 */
	public static boolean compareTwoString(String source, String target){
		if(source == null || target == null){
			return false;
		}
		//遍历比对
		char[] arr1 = source.toCharArray();
		Arrays.sort(arr1);
		char[] arr2 = target.toCharArray();
		Arrays.sort(arr2);
		String s1 = String.valueOf(arr1);
		String s2 = String.valueOf(arr2);
		return s1.equals(s2);
	}
	
	/**
	 * 随机生成指定个数的数组
	 * @param max	最大值
	 * @param count	个数
	 * @return
	 */
	public static int[] randomInt(int max, int count){
		if(count >= max){
			int[] arr = new int[max];
			for(int i=0;i<max;i++){
				arr[i] = i;
			}
			return arr;
		}
		Set<Integer> set = new HashSet<Integer>();
		int[] arr = new int[count];
		Random rand = new Random();
		for(int i=0;i<count;i++){
			int r = rand.nextInt(max);
			while(set.contains(r)){
				r = rand.nextInt(max);
			}
			set.add(r);
			arr[i] = r;
		}
		return arr;
	}
	
	/**
	 * 批量初始化prop目录
	 * @param propFolderName
	 */
	public static void initProp(String... propFolderNames){
		//类似遍历resources/logAnalyse目录
		for(String propFolderName : propFolderNames){
			String path = Thread.currentThread().getContextClassLoader().getResource(propFolderName).getFile();
			String[] arr = FileTools.getFileNameArray(path, ".properties");
			for(String s : arr){
				PropKit.use(propFolderName + "/" + s);
			}
		}
	}
	
	/**
	 * 批量初始化prop目录
	 * @param propFolderName prop目录
	 * @param ignorePropName 忽略的prop文件名
	 */
	public static void initPropIgnore(String propFolderName, String ignorePropName){
		String prePath = "";
		if(!"".contentEquals(propFolderName)) {
			prePath = propFolderName + "/";
		}
		String path = Thread.currentThread().getContextClassLoader().getResource(propFolderName).getFile();
		String[] arr = FileTools.getFileNameArray(path, ".properties");
		for(String s : arr){
			if(!s.equals(ignorePropName)) {
				PropKit.use(prePath + s);
				log.info("配置文件[{}]加载成功...", s);
			}
		}
	}
	
	/**
	  *  去除字符串的换行符
	 * @param input
	 * @return
	 */
	public static String trimLineBreak(String input) {
		if(input == null) {
			return null;
		}
		return input.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "");
	}
	
	/**
	 * 按指定分隔符分隔字符串
	 * @param str
	 * @param divide
	 * @return
	 */
	public static String[] split(String str, char divide){
		String[] ret = new String[count(str,divide) + 1];
		char[] arr = str.toCharArray();
		int length = arr.length;
		StringBuffer buff = new StringBuffer();
		int cnt = 0;
		int step = 0;
		for(char c : arr){
			step++;
			if(c == divide && step == length){
				ret[cnt++] = buff.toString();
				break;
			}
			if(c == divide ||  step == length){
				if(step == length){
					buff.append(c);
				}
				ret[cnt++] = buff.toString();
				buff = new StringBuffer();
			}else{
				buff.append(c);
			}
		}
		return ret;
	}
	
	/**
	 * 统计字符串内指定字符的出现个数
	 * @param str
	 * @param target
	 * @return
	 */
	public static int count(String str, char target){
		int cnt = 0;
		char[] arr = str.toCharArray();
		for(char c : arr){
			if(c == target){
				cnt++;
			}
		}
		return cnt;
	}
}
