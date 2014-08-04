package org.exoplatform.com.blog.service;

import javax.jcr.Node;
import java.util.List;

/**
 * Created by toannh on 8/4/14.
 */
public interface IBlogService {

  /**
   * count Node by Year
   * @param year
   * @return
   */
  public List<Node> getNodeTotalByYear(String nodeType, String node_date, int year) throws Exception;
}
