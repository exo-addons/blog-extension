package org.exoplatform.com.blog.service.util;

/**
 * Created by toannh on 8/5/14.
 */

import org.exoplatform.com.blog.service.entity.BlogArchive;

import java.util.*;

public class BlogArchiveUtil<E, K> extends HashMap {

  private Map<K, BlogArchive> blogArchive = new HashMap<K, BlogArchive>();
  private Map<Object, Integer> month = new HashMap<Object, Integer>();

  public void add(K year, E month) {
    BlogArchive blogArchive;
    if (!this.blogArchive.containsKey(year)) { // year doesnt exits
      this.month = new HashMap<Object, Integer>();
      blogArchive = new BlogArchive();
      blogArchive.setYear_post(1);
      this.month.put(month, 1);
      blogArchive.setMonth(this.month);
      this.blogArchive.put(year, blogArchive);
    } else { // year exits
      blogArchive = this.blogArchive.get(year);
      blogArchive.setYear_post(blogArchive.getYear_post() + 1);
      this.month = blogArchive.getMonth();
      if (!this.month.containsKey(month)) { //month of year doesnt exits
        this.month.put(month, 1);
      } else {
        this.month.put(month, this.month.get(month) + 1);
      }
      blogArchive.setMonth(this.month);
      this.blogArchive.put(year, blogArchive);
    }
    super.put(year, blogArchive);
  }

  public int getCount(K year, E month) {
    if (!blogArchive.containsKey(year)) {
      return 0;
    }
    if (month == null) {
      return this.blogArchive.get(year).getYear_post();
    }
    if (!this.blogArchive.get(year).getMonth().containsKey(month)) {
      return 0;
    }
    return this.blogArchive.get(year).getMonth().get(month);
  }

  public Map<K, BlogArchive> getBlogArchive() {
    return blogArchive;
  }
}
