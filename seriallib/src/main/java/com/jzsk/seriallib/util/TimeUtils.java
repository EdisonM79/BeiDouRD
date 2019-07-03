package com.jzsk.seriallib.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by DeW on 2017/4/1.
 */

public class TimeUtils {

    public static SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");

    public static SimpleDateFormat sSmsDateFormat = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
    
    public static long getCurrentTimeJTSerialform(){
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        String dateStr = sSimpleDateFormat.format(date);
        long strToLong = 0;
        try{
             strToLong = Long.parseLong(dateStr);
        }catch (NumberFormatException ex){
            ex.printStackTrace();
        }finally {
            return strToLong;
        }
    }
    
    public static String getCurrentTimeSmsform(){
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        String dateStr = sSmsDateFormat.format(date);
        return dateStr;
    }
    public static long getTimeJT808form(long time){
        Date date = new Date(time);
        String dateStr = sSimpleDateFormat.format(date);
        long strToLong = 0;
        try{
             strToLong = Long.parseLong(dateStr);
        }catch (NumberFormatException ex){
            ex.printStackTrace();
        }finally {
            return strToLong;
        }
    }
    
    public static void main(String[] args) {
//    	long time = getCurrentTimeJT808form();
    	Calendar calendar = Calendar.getInstance();
    	byte year =(byte) (calendar.get(Calendar.YEAR) - 2000);
    	byte month=(byte) (calendar.get(Calendar.MONTH)+1);
    	byte day =(byte) calendar.get(Calendar.DAY_OF_MONTH);
    	byte hour =(byte) calendar.get(Calendar.HOUR_OF_DAY);
    	byte minute =(byte) calendar.get(Calendar.MINUTE);
    	byte second =(byte) calendar.get(Calendar.SECOND);
    	
		System.out.println(ArrayUtils.bytesToHexString(ArrayUtils.concatenate(
				IntegerUtils.asBytes(year),
				IntegerUtils.asBytes(month),
				IntegerUtils.asBytes(day),
				IntegerUtils.asBytes(hour),
				IntegerUtils.asBytes(minute),
				IntegerUtils.asBytes(second))));
	}
}
