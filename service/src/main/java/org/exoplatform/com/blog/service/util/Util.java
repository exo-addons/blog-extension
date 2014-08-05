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

//  public static void main(String[] args) {
//    int year = 2014;
//    System.out.println(getFirstDayOfYear(year));
//    System.out.println(getStrLastDayOfYear(year));
//  }
}
