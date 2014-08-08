package org.exoplatform.com.blog.service;

import javax.jcr.Node;
import java.util.List;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 4, 2014
 */
public interface IBlogService {

  /**
   * Get all blog archive years
   * @return
   */
  public List<Integer> getArchiveYears();

  /**
   * Get all archive month by year
   * @param year
   * @return
   */
  public List<Integer> getArchiveMonths(int year);

  /**
   * Get total post of year
   * @param year
   * @return
   */
  public int getArchivesCountInYear(int year);

  /**
   * Get total post of  month
   * @param year
   * @param month
   * @return
   */
  public int getArchivesCountInMonth(int year, int month);

  /**
   * Get all blogs by year/month
   * @param year
   * @param month
   * @return
   */
  public List<Node> getBlogs(int year, int month);

  /**
   * Increase post count from blog-archive cached table. when add new a post.
   * @param blogNode
   */
  public void addBlog(Node blogNode);

  /**
   * Decrease post count from blog-archive cached table. when remove a post.
   * @param blogNode
   */
  public void removeBlog(Node blogNode);
}
