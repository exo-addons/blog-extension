package org.exoplatform.com.blog.service.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by toannh on 8/5/14.
 */
public class BlogArchive extends HashMap{
  private int year_post;
  private Map<Object, Integer> month;

  public int getYear_post() {
    return year_post;
  }

  public void setYear_post(int year_post) {
    this.year_post = year_post;
  }

  public Map<Object, Integer> getMonth() {
    return month;
  }

  public void setMonth(Map<Object, Integer> month) {
    this.month = month;
  }
}
