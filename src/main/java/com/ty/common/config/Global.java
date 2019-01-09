/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.ty.common.config;

import com.google.common.collect.Maps;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.Customer;
import com.ty.common.utils.PropertiesLoader;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 全局配置类
 * @author ThinkGem
 * @version 2014-06-25
 */
public class Global {

	/**
	 * 当前对象实例
	 */
	private static Global global = new Global();

	/**
	 * 保存全局属性值
	 */
	private static Map<String, String> map = Maps.newHashMap();

	private static Map<String,Integer> MINUTESENDMOBILECOUNT = Maps.newHashMap();

	private static Map<String,Integer> HOURSENDMOBILECOUNT = Maps.newHashMap();

	private static Map<String,Integer> DAYSENDMOBILECOUNT = Maps.newHashMap();

	/**
	 * 属性文件加载对象
	 */
	private static PropertiesLoader loader = new PropertiesLoader("system.properties");

	/**
	 * 显示/隐藏
	 */
	public static final String SHOW = "1";
	public static final String HIDE = "0";

	/**
	 * 是/否
	 */
	public static final String YES = "1";
	public static final String NO = "0";

	/**
	 * 对/错
	 */
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	/**
	 * 上传文件基础虚拟路径
	 */
	public static final String USERFILES_BASE_URL = "/userfiles/";

	/**
	 * 获取当前对象实例
	 */
	public static Global getInstance() {
		return global;
	}

	/**
	 * post请求最多数据条数
	 */
	public static int POSTCOUNT = 10000;

	/**
	 * get请求最多数据条数
	 */
	public static int GETCOUNT = 1000;

	/**
	 * 一个手机号一天内最多发送条数
	 */
	public static int MAXSENDCOUNT = 50;

	public static int PUSHCOUNT = 3;

	public static String CLIENT_REDIS_KEY = "client:msgRecord";

	public static String SDK_REDIS_KEY = "sdk:msgRecord";

	/**
	 * 是否启动区域限制，默认不启用
	 */
	public static int isDm = 0;
	public static String MINUTE_SEND_MOBILE_COUNT = "minuteSendMobileCount";
	public static String HOUR_SEND_MOBILE_COUNT = "hourSendMobileCount";
	public static String DAY_SEND_MOBILE_COUNT = "daySendMobileCount";
	public static final String CACHE_CUSTOMER_TUNNEL  = "CUSTOMER_TUNNEL";
	public static final String CACHE_CUSTOMER_INFO  = "customerCache";
	public static final String CACHE_KEYWORDS_INFO  = "keyWordsCache";
	public static final String CACHE_BLACK_INFO  = "blackCache";
	public static final String CACHE_TPL_INFO = "tplCache";
	public static final String CACHE_CUSTOMER_BLACK_INFO  = "customerBlackCache";
	public static final String CLEAR_CACHE_PLATEFORMBLACK = "clearPlateformBlackList";
	public static final String CLEAR_CACHE_CUSTOMERBLACK = "clearCustomerBlackList";
	public static final String  CLEAR_CACHE_CUSTOMER = "clearCustomer";
	public static final String CLEAR_CACHE_KEYWORDS = "clearKeywords";
	public static final String CLEAR_CACHE_TEMPLATE = "clearTemplate";

	public static final String CACHE_CUSTOMER_COST_INFO  = "customerCost:";

	public static String SUBMIT_LOG_REDIS_KEY = "messageSubmitLog";
	public static String SEND_RECORD_REDIS_KEY = "sendRecord";
	public static String SEND_REPORT_REDIS_KEY = "sendReport";
	public static String SEND_RESPONSE_REDIS_KEY = "sendResponse";
	public static String  FTCHREPORT_REDIS_KEY = "ftchReportPara";
	public static String FTCHRERPLY_REDIS_KEY = "ftchReplyPara";
	public static String  FTCHREPORTKLWS_REDIS_KEY ="ftchReportKlwsPara";

	public static int REDIS_GET_SWITCH = 1;
	/**
	 * 获取配置
	 * @see {fns:getConfig('adminPath')}
	 */
	public static String getConfig(String key) {
		String value = map.get(key);
		if (value == null){
			value = loader.getProperty(key);
			map.put(key, value != null ? value : StringUtils.EMPTY);
		}
		return value;
	}


	/**
	 * 页面获取常量
	 * @see {fns:getConst('YES')}
	 */
	public static Object getConst(String field) {
		try {
			return Global.class.getField(field).get(null);
		} catch (Exception e) {
			// 异常代表无配置，这里什么也不做
		}
		return null;
	}

	/**
	 * 获取工程路径
	 * @return
	 */
	public static String getProjectPath(){
		// 如果配置了工程路径，则直接返回，否则自动获取。
		String projectPath = Global.getConfig("projectPath");
		if (StringUtils.isNotBlank(projectPath)){
			return projectPath;
		}
		try {
			File file = new DefaultResourceLoader().getResource("").getFile();
			if (file != null){
				while(true){
					File f = new File(file.getPath() + File.separator + "src" + File.separator + "main");
					if (f == null || f.exists()){
						break;
					}
					if (file.getParentFile() != null){
						file = file.getParentFile();
					}else{
						break;
					}
				}
				projectPath = file.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return projectPath;
	}

	public static void clearMinuteSendMobileCount(){
		MINUTESENDMOBILECOUNT.clear();
	}

	public static void clearHourSendMobileCount(){
		HOURSENDMOBILECOUNT.clear();
	}

	public static void clearDaySendMobileCount(){
		DAYSENDMOBILECOUNT.clear();
	}

	public static void incrementSendCount(String customerId,String mobile){
		Integer minuteCount = MINUTESENDMOBILECOUNT.get(customerId+"_"+mobile);
		if(minuteCount == null){
			MINUTESENDMOBILECOUNT.put(customerId+"_"+mobile,1);
		}else{
			MINUTESENDMOBILECOUNT.put(customerId+"_"+mobile,minuteCount+1);
		}

		Integer hourCount = HOURSENDMOBILECOUNT.get(customerId+"_"+mobile);
		if(hourCount == null){
			HOURSENDMOBILECOUNT.put(customerId+"_"+mobile,1);
		}else{
			HOURSENDMOBILECOUNT.put(customerId+"_"+mobile,hourCount+1);
		}

		Integer dayCount = DAYSENDMOBILECOUNT.get(customerId+"_"+mobile);
		if(dayCount == null){
			DAYSENDMOBILECOUNT.put(customerId+"_"+mobile,1);
		}else{
			DAYSENDMOBILECOUNT.put(customerId+"_"+mobile,dayCount+1);
		}
	}

	public static boolean checkSendCount(Customer customer, String mobile){
		String customerId = customer.getId();
		boolean result = true;
		Integer minuteCount = MINUTESENDMOBILECOUNT.get(customerId+"_"+mobile);
		if(minuteCount != null && minuteCount >= customer.getMinuteSendCountLimit()){
			result = false;
		}

		Integer hourCount = HOURSENDMOBILECOUNT.get(customerId+"_"+mobile);
		if(hourCount != null && hourCount >= customer.getHourSendCountLimit()){
			result = false;
		}

		Integer dayCount = DAYSENDMOBILECOUNT.get(customerId+"_"+mobile);
		if(dayCount != null && dayCount >= customer.getDaySendCountLimit()){
			result = false;
		}
		return result;
	}

}
