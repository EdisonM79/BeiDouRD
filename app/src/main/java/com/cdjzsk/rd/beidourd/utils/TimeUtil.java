package com.cdjzsk.rd.beidourd.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * java时间格式的转换
 * Created by qqg on 2017/10/11.
 */
public class TimeUtil {
	/**
	 * Date 类型转化为 String 类型
	 * @param date
	 * @return yyyy-MM-dd HH:mm:ss 格式的时间
	 */
	public String dateToString(Date date){
		String strDateFormat = "yyyy年MM月dd日 HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
		return sdf.format(date);
	}

	/**
	 * Date 类型转化为 String 类型
	 * @param date
	 * @return  yyyy-MM-dd 格式的时间
	 */
	public String dateToString1(Date date){
		String strDateFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
		return sdf.format(date);
	}

	/**
	 * String 类型转化为 Date 类型
	 * @param strTime       String 类型时间
	 * @param formatType    时间格式
	 * @return              Date 类型的时间
	 * @throws ParseException
	 */
	public static Date stringToDate(String strTime, String formatType)
			throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(formatType);
		Date date = null;
		date = formatter.parse(strTime);
		return date;
	}

	/**
	 * String 类型数据转化为 Date 类型数据
	 * @param strTime   String 类型时间
	 * @return          Date 类型时间，转换后忽略时分秒
	 * @throws ParseException
	 */
	public static Date stringToDate(String strTime)
			throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		date = formatter.parse(strTime);
		return date;
	}

	/**
	 * String 类型转化为 Date 类型
	 * @param strTime   String 类型时间
	 * @return          Date 类型时间，转化后保存时分秒
	 * @throws ParseException
	 */
	public static Date stringToDate1(String strTime)
			throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		date = formatter.parse(strTime);
		return date;
	}
	/**
	 * 获取时分秒
	 * @param date
	 * @return  HH:mm:ss 格式的时分秒
	 */
	public static String getTimeShort(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String dateString = formatter.format(date);
		return dateString;
	}

	/**
	 * Date 类型转化为 Long 类型
	 * @param date
	 * @return
	 */
	public static Long dateToLong(Date date){
		return date.getTime();
	}
	/**
	 * Long 类型转化为 String 类型
	 * @param time
	 * @return
	 */
	public static Date longToDate(Long time){
		return new Date(time);
	}
}