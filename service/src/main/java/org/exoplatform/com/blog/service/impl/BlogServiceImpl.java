package org.exoplatform.com.blog.service.impl;

import org.exoplatform.com.blog.service.IBlogService;

import javax.jcr.Node;
import java.util.List;

/**
 * Created by toannh on 8/4/14.
 */
public class BlogServiceImpl extends BaseService implements IBlogService {

  @Override
  public List<Node> getNodeTotalByYear(String nodeType, String node_date, int year) throws Exception{
    List<Node> lstNodes = getNodeByYear(nodeType, node_date, year);
    return lstNodes;
  }
}
