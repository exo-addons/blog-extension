package org.exoplatform.com.blog.service;

import org.exoplatform.com.blog.service.entity.BlogArchive;
import org.exoplatform.com.blog.service.util.BlogArchiveUtil;

import javax.jcr.Node;
import java.util.List;
import java.util.Map;

/**
 * Created by toannh on 8/4/14.
 */
public interface IBlogService {

  /**
   * Get All node of year
   * @param nodeType exo:blog
   * @param node_date exo:dateCreated
   * @param year 2014
   * @param selectOptions select query option
   * @return List all year's nodes
   * @throws Exception
   */
  public List<Node> getBlogs(String nodeType, String node_date, int year, String selectOptions) throws Exception;

  public Map<Integer, BlogArchive> getBlogArchive();
}
