package com.example.uhfsdkdemo.history;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
		private static SimpleDateFormat sf = null;
		private static String formatString = "yyyy年MM月dd日 HH:mm:ss";
	  
		/*获取系统时间 格式为："yyyy/MM/dd "*/
		public static String getCurrentDate() {
		    Date d = new Date();
		    sf = new SimpleDateFormat(formatString);
		    return sf.format(d);
		}

	    /*时间戳转换成字符窜*/
	    public static String getDateToString(long time) {
	        Date d = new Date(time * 1000);
	        sf = new SimpleDateFormat(formatString);
	        return sf.format(d);
	    }

	    /*将字符串转为时间戳*/
	    public static long getStringToDate(String time) {
	        sf = new SimpleDateFormat(formatString);
	        Date date = new Date();
	        try{
	            date = sf.parse(time);
	        } catch(ParseException e) {
	            e.printStackTrace();
	        }
	        return date.getTime();
	    }
	    
	    /**
	     * 获得当前时间戳
	     * @return
	     */
	    public static String getCurrentTimestamp() {
	    	long time = System.currentTimeMillis() / 1000; //获取系统时间的10位的时间戳
	    	String str = String.valueOf(time);
	    	return str;
	    }

}
