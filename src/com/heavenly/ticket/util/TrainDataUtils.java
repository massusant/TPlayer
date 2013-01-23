package com.heavenly.ticket.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
public class TrainDataUtils {
	
	public static final String saveLoginCodePath = "D://1.jpg";	
	public static final String saveSumbitCodePath = saveLoginCodePath;
	public static final String logCodeUrd = "https://dynamic.12306.cn/otsweb/passCodeAction.do?rand=sjrand";
	
	/**
	 * 加载站点信息
	 */
	public static Map<String,String> stationNames = new HashMap<String, String>();
	static {
		String station_name_string = getTextFromFile("station_name_java.js");
		for (String station_name : StringUtils.split(station_name_string, "@")) {
			String[] station = StringUtils.split(station_name, "|");
			stationNames.put(station[1], station[2]);
		}
	}
	
	/**
	 * 加载座位信息
	 */
	public static Map<String,String> seatNameValueMap = new LinkedHashMap<String,String>();//座位在官网提交数据对应的值
	public static Map<String,Integer> seatNameIndexMap = new LinkedHashMap<String,Integer>();//座位在官网搜索数据中所在的序号
	static{
		seatNameValueMap.put("商务座","9");
		seatNameIndexMap.put("商务座", 5);
		
		seatNameValueMap.put("特等座", "P");
		seatNameIndexMap.put("特等座", 6);
		
		seatNameValueMap.put("一等座", "M");
		seatNameIndexMap.put("一等座", 7);
		
		seatNameValueMap.put("二等座","O");
		seatNameIndexMap.put("二等座", 8);
		
		seatNameValueMap.put("高级软卧", "6");
		seatNameIndexMap.put("高级软卧", 9);
		
		seatNameValueMap.put("软卧", "4");
		seatNameIndexMap.put("软卧", 10);
		
		seatNameValueMap.put("硬卧", "3");
		seatNameIndexMap.put("硬卧", 11);
		
		seatNameValueMap.put("软座", "2");
		seatNameIndexMap.put("软座", 12);
		
		seatNameValueMap.put("硬座", "1");
		seatNameIndexMap.put("硬座", 13);
		
		seatNameValueMap.put("上铺", "3");
		seatNameValueMap.put("下铺", "1");
		seatNameValueMap.put("中铺", "2");
		seatNameValueMap.put("随机", "0");
	}
	
	public static String today;
	static{
		today = DateShowUtils.getFormatedDateString(new Date());
	}
	
	/**
	 * 订票帐号信息
	 */
//	public static String query_train_date = "2013-01-28";//订单时间
	public static String username = "weichao222";//登录帐号
	public static String password = "weichao_222";//登录密码
	public static String name = "魏超";//姓名
	public static String cardid = "510725199003242416";//身份证
	public static String mobile = "15116335086";//手机

	private static String getTextFromFile(InputStream inputStream){
	      BufferedReader freader = null;
	      StringBuffer text = new StringBuffer();
	      //读文件
	      try {
	          freader=new BufferedReader(new InputStreamReader(inputStream));
	          String text1 ="";
	         while((text1=freader.readLine())!=null){
	            text.append(text1+"\n");
	         }
	         freader.close();
	         System.out.println("替换操作成功，开始保存文件");
	      } catch (FileNotFoundException e) {
	         // TODO Auto-generated catch block
	       } catch (IOException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      } finally{
	         try {
	            freader.close();
	         } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	         }
	         
	      }
	      return text.toString();
	   }
	   private static String getTextFromFile(String path){
	      return getTextFromFile(TrainDataUtils.class.getClassLoader().getResourceAsStream(path));
	   }
}

