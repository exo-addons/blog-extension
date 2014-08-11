package org.exoplatform.com.blog.service.util;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Created by toannh on 8/4/14.
 */

public class Util {
  private static Log log = ExoLogger.getExoLogger("BlogService");
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

  /**
   *
   * @param info message
   * @param error message
   */
  public static void log(String info, String error){
    if(log.isErrorEnabled()) log.error(error);
    if(log.isInfoEnabled()) log.info(info);
  }
}
