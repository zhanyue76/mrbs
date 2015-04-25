package com.alex.mrbs.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public  class MyTimerUtil {
  
  public  static boolean timeValidate(String startimestr, String endtimestr)
			throws ParseException {
			  DateFormat df= new SimpleDateFormat('HH:mm:ss');
			  Date systime = new Date();
		  Date starttime = df.parse(startimestr);
		  Date endtime = df.parse(endtimestr);
		  String systimestr =df.format(systime);
		  systime = df.parse(systimestr);
		  if(systime.before(starttime)||systime.after(endtime)){
			  return false;
		  }else{
			return true;
		}
	}
  
}
