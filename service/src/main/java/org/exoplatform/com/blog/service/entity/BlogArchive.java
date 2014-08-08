package org.exoplatform.com.blog.service.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 5, 2014
 */
public class BlogArchive extends HashMap{
  private int year_post;
  private Map<Integer, Integer> month;

  public int getYear_post() {
    return year_post;
  }

  public void setYear_post(int year_post) {
    this.year_post = year_post;
  }

  public Map<Integer, Integer> getMonth() {
    return month;
  }

  public void setMonth(Map<Integer, Integer> month) {
    this.month = month;
  }
}
