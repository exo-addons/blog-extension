package org.exoplatform.com.blog.service.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by toannh on 8/4/14.
 */
public class Util {
  public static final String EXO_COMMENT_NODE_TYPE = "exo:comments";
  public static final String EXO_COMMENT_DATE = "exo:commentDate";
  private static final String TIME_FORMAT_TAIL = "T00:00:00.000";
  private static final SimpleDateFormat formatDateTime = new SimpleDateFormat();

  static {
    formatDateTime.applyPattern("yyyy-MM-dd");
  }

  public static String getFirstDayOfYear(int year) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.WEEK_OF_YEAR, 1);
    cal.set(Calendar.DAY_OF_WEEK, 1);

    return formatDateTime.format(cal.getTime()) + TIME_FORMAT_TAIL;
  }

  public static String getStrLastDayOfYear(int year) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year + 1);
    cal.set(Calendar.WEEK_OF_YEAR, 1);
    cal.set(Calendar.DAY_OF_WEEK, 1);
    return formatDateTime.format(cal.getTime()) + TIME_FORMAT_TAIL;
  }

  public static String getStrFirstDayOfMonth(int year, int month){
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    return formatDateTime.format(cal.getTime()) + TIME_FORMAT_TAIL;
  }
  public static String getStrLastDayOfMonth(int year, int month){
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month+1);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    return formatDateTime.format(cal.getTime()) + TIME_FORMAT_TAIL;
  }

  public static String numberToWord(int number) {
    switch (number) {
      case 0:
        return "January";
      case 1:
        return "February";
      case 2:
        return "March";
      case 3:
        return "April";
      case 4:
        return "May";
      case 5:
        return "Jun";
      case 6:
        return "July";
      case 7:
        return "August";
      case 8:
        return "September";
      case 9:
        return "October";
      case 10:
        return "November";
      case 11:
        return "December";
    }
    return "";
  }

  public static void main(String[] args) {
    int year = 2014;
    int month = 1;
    System.out.println(getStrFirstDayOfMonth(year, month));
    System.out.println(getStrLastDayOfMonth(year, month));
//    System.out.println(numberToWord(year));
//    System.out.println(getStrLastDayOfYear(year));
  }
}
