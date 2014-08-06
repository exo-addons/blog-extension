package org.exoplatform.com.blog.service.impl;

import org.exoplatform.com.blog.service.IBlogService;
import org.exoplatform.com.blog.service.entity.BlogArchive;
import org.exoplatform.com.blog.service.util.BlogArchiveUtil;
import org.exoplatform.com.blog.service.util.Util;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;

import javax.jcr.Node;
import java.util.*;

/**
 * Created by toannh on 8/4/14.
 */
public class BlogServiceImpl extends BaseService implements IBlogService {

  private boolean initData = true;
  public static String BLOG_NODE = "exo:blog";

  private static BlogArchiveUtil<Integer, Integer> blogArchive = new BlogArchiveUtil<Integer, Integer>();

  public BlogServiceImpl(RepositoryService repoService, SessionProviderService sessionProviderService) {
    this.repoService = repoService;
    this.sessionProviderService = sessionProviderService;

    initBlogArchive();
    setInitData(false);
  }

  void initBlogArchive() {
    if (isInitData()) {
      try {
        List<Node> allNode = getAllNode(BLOG_NODE);
        Calendar cal = null;
        for (Node node : allNode) {
          cal = node.getProperty("exo:dateCreated").getValue().getDate();
          int _year = cal.get(Calendar.YEAR);
          int _month = cal.get(Calendar.MONTH);
          blogArchive.add(_year, _month);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public boolean isInitData() {
    return initData;
  }

  public void setInitData(boolean initData) {
    this.initData = initData;
  }

  public BlogArchiveUtil<Integer, Integer> getBlogArchive() {
    return blogArchive;
  }

  @Override
  public List<Integer> getArchiveYears() {
    List<Integer> result = new ArrayList<Integer>();
    HashMap<Integer, BlogArchive> blogArchiveHashMap = this.blogArchive;
    for (Map.Entry<Integer, BlogArchive> entry : blogArchiveHashMap.entrySet()) {
      result.add(entry.getKey());
    }
    return result;
  }

  @Override
  public List<Integer> getArchiveMonths(int year) {
    BlogArchive month = this.blogArchive.getBlogArchive().get(year);
    List<Integer> result = new ArrayList<Integer>();
    Map<Object, Integer> monthHashmap = month.getMonth();
    for (Map.Entry<Object, Integer> entry : monthHashmap.entrySet()) {
      result.add(Integer.valueOf(entry.getKey().toString()));
    }
    return result;
  }

  @Override
  public int getArchivesCountInYear(int year) {
    return this.blogArchive.getBlogArchive().get(year).getYear_post();
  }

  @Override
  public int getArchivesCountInMonth(int year, int month) {
    return this.blogArchive.getBlogArchive().get(year).getMonth().get(month);
  }

  @Override
  public List<Node> getBlogs(int year, int month) {
    try {
      return getAllNode(BLOG_NODE, Util.getStrFirstDayOfMonth(year, month), Util.getStrLastDayOfMonth(year, month));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  @Override
  public void addBlog(Node blogNode) {
    try {
      if (blogNode.isNodeType(BlogServiceImpl.BLOG_NODE)) {
        Calendar cal = blogNode.getProperty("exo:dateCreated").getDate();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        BlogArchive _blogYearArchive = blogArchive.getBlogArchive().get(year);
        blogArchive.getBlogArchive().get(year).setYear_post(_blogYearArchive.getYear_post() + 1);
        Map<Object, Integer> monthByYear = _blogYearArchive.getMonth();

        if (monthByYear.containsKey(month)) {
          blogArchive.getBlogArchive().get(year).getMonth().put(month, monthByYear.get(month) + 1);
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void removeBlog(Node blogNode) {
    try {
      if (blogNode.isNodeType(BlogServiceImpl.BLOG_NODE)) {
        Calendar cal = blogNode.getProperty("exo:dateCreated").getDate();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        BlogArchive _blogYearArchive = blogArchive.getBlogArchive().get(year);
        blogArchive.getBlogArchive().get(year).setYear_post(_blogYearArchive.getYear_post() - 1);
        Map<Object, Integer> monthByYear = _blogYearArchive.getMonth();

        if (monthByYear.containsKey(month)) {
          blogArchive.getBlogArchive().get(year).getMonth().put(month, monthByYear.get(month) - 1);
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
