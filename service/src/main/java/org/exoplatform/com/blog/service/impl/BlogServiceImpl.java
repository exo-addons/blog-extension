package org.exoplatform.com.blog.service.impl;

import org.exoplatform.com.blog.service.IBlogService;
import org.exoplatform.com.blog.service.util.BlogArchiveUtil;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;

import javax.jcr.Node;
import java.util.*;

/**
 * Created by toannh on 8/4/14.
 */
public class BlogServiceImpl extends BaseService implements IBlogService {

  private boolean initData = true;
  private static String BLOG_NODE = "exo:blog";

  private BlogArchiveUtil archiveUtil = new BlogArchiveUtil();

  public BlogServiceImpl(RepositoryService repoService, SessionProviderService sessionProviderService) {
    this.repoService = repoService;
    this.sessionProviderService = sessionProviderService;

    initBlogArchive();
    setInitData(false);
  }

  @Override
  public List<Node> getBlogs(String nodeType, String node_date, int year, String selectOptions) throws Exception {
    return getAllNode(nodeType);
  }

  @Override
  public BlogArchiveUtil getBlogArchive() {
    return archiveUtil;
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
          archiveUtil.add(_year, _month);
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
}
