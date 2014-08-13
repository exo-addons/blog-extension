/*
 * Copyright (C) 2003-2014 eXo Platform SEA.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */


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

  public static int getInt(String value, int defaultValue){
    try{
      return Integer.parseInt(value);
    }catch(NumberFormatException ex){
      return defaultValue;
    }
  }

//  public static Node getNode(String postPath) {
//    try {
//      Session session = getSession();
//      return (Node)session.getItem(postPath);
//    }catch(Exception ex){if(log.isErrorEnabled()){log.error(ex.getMessage());}}
//    return null;
//  }


}
