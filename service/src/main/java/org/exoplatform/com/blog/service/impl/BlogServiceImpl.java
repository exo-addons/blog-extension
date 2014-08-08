package org.exoplatform.com.blog.service.impl;

import org.exoplatform.com.blog.service.IBlogService;
import org.exoplatform.com.blog.service.entity.BlogArchive;
import org.exoplatform.com.blog.service.util.BlogArchiveUtil;
import org.exoplatform.com.blog.service.util.Util;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.util.*;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 4, 2014
 */
public class BlogServiceImpl implements IBlogService {

  private boolean initData = true;
  public static String BLOG_NODE = "exo:blog";
  Log log = ExoLogger.getExoLogger(BlogServiceImpl.class);

  private String repo = "repository";
  private String ws = "collaboration";

  RepositoryService repoService;
  SessionProviderService sessionProviderService;


  private static BlogArchiveUtil<Integer, Integer> blogArchive = new BlogArchiveUtil<Integer, Integer>();

  public BlogServiceImpl(RepositoryService repoService, SessionProviderService sessionProviderService) {
    this.repoService = repoService;
    this.sessionProviderService = sessionProviderService;
    try {
      this.repo = repoService.getCurrentRepository().getConfiguration().getName();
      this.ws = repoService.getCurrentRepository().getConfiguration().getDefaultWorkspaceName();
    } catch (Exception ex) {
      log.info("Using default repository & workspace");
      ex.printStackTrace();
    }
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

//  public BlogArchiveUtil<Integer, Integer> getBlogArchive() {
//    return blogArchive;
//  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Integer> getArchiveYears() {
    List<Integer> result = new ArrayList<Integer>();
    HashMap<Integer, BlogArchive> blogArchiveHashMap = this.blogArchive;
    for (Map.Entry<Integer, BlogArchive> entry : blogArchiveHashMap.entrySet()) {
      result.add(entry.getKey());
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Integer> getArchiveMonths(int year) {
    BlogArchive month = this.blogArchive.getBlogArchive().get(year);
    List<Integer> result = new ArrayList<Integer>();
    Map<Integer, Integer> monthHashmap = month.getMonth();
    for (Map.Entry<Integer, Integer> entry : monthHashmap.entrySet()) {
      result.add(Integer.valueOf(entry.getKey().toString()));
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getArchivesCountInYear(int year) {
    return this.blogArchive.getBlogArchive().get(year).getYear_post();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int getArchivesCountInMonth(int year, int month) {
    return this.blogArchive.getBlogArchive().get(year).getMonth().get(month);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Node> getBlogs(int year, int month) {
    try {
      return getAllNode(BLOG_NODE, Util.getStrFirstDayOfMonth(year, month), Util.getStrLastDayOfMonth(year, month));
    } catch (Exception ex) {
      log.error(ex.getMessage());
    }
    return null;
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public void addBlog(Node blogNode) {
    try {
      if (blogNode.isNodeType(BlogServiceImpl.BLOG_NODE)) {
        Calendar cal = new GregorianCalendar();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        BlogArchive _blogYearArchive = blogArchive.getBlogArchive().get(year);
        blogArchive.getBlogArchive().get(year).setYear_post(_blogYearArchive.getYear_post() + 1);
        Map<Integer, Integer> monthByYear = _blogYearArchive.getMonth();

        if (monthByYear.containsKey(month)) {
          blogArchive.getBlogArchive().get(year).getMonth().put(month, monthByYear.get(month) + 1);
        }
      }
    } catch (Exception ex) {
      log.error(ex.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeBlog(Node blogNode) {
    try {
      if (blogNode.isNodeType(BlogServiceImpl.BLOG_NODE)) {
        Calendar cal = blogNode.getProperty("exo:dateCreated").getDate();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        BlogArchive _blogYearArchive = blogArchive.getBlogArchive().get(year);
        blogArchive.getBlogArchive().get(year).setYear_post(_blogYearArchive.getYear_post() - 1);
        Map<Integer, Integer> monthByYear = _blogYearArchive.getMonth();

        if (monthByYear.containsKey(month)) {
          blogArchive.getBlogArchive().get(year).getMonth().put(month, monthByYear.get(month) - 1);
        }
      }
    } catch (Exception ex) {
      log.error(ex.getMessage());
    }
  }


  /**
   * Get All node of element
   *
   * @param nodeElement
   * @return
   * @throws Exception
   */
  protected List<Node> getAllNode(String nodeElement) throws Exception {
    Session session = getSession();
    List<Node> rs = new ArrayList<Node>();
    StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ").append(nodeElement);
    queryBuilder.append(" ORDER BY exo:dateCreated DESC ");
    QueryManager queryManager = session.getWorkspace().getQueryManager();
    Query query = queryManager.createQuery(queryBuilder.toString(), Query.SQL);

    NodeIterator nodes = query.execute().getNodes();
    while (nodes.hasNext()) {
      rs.add(nodes.nextNode());
    }
    return rs;
  }

  /**
   * Get All node by node, month
   *
   * @param nodeElement
   * @param firstDayOfMonth
   * @param lastDayOfMonth
   * @return
   * @throws Exception
   */
  protected List<Node> getAllNode(String nodeElement, String firstDayOfMonth, String lastDayOfMonth) throws Exception {
    Session session = getSession();
    List<Node> rs = new ArrayList<Node>();
    StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ").append(nodeElement);
    queryBuilder.append(" WHERE exo:dateCreated >= TIMESTAMP '" + firstDayOfMonth + "' ");
    queryBuilder.append(" AND   exo:dateCreated <= TIMESTAMP '" + lastDayOfMonth + "' ");
    queryBuilder.append(" ORDER BY exo:dateCreated DESC ");
    QueryManager queryManager = session.getWorkspace().getQueryManager();
    Query query = queryManager.createQuery(queryBuilder.toString(), Query.SQL);

    NodeIterator nodes = query.execute().getNodes();
    while (nodes.hasNext()) {
      rs.add(nodes.nextNode());
    }
    return rs;
  }

  /**
   * Get session
   *
   * @return
   * @throws Exception
   */
  protected Session getSession() throws Exception {
    ManageableRepository repository = repoService.getRepository(this.repo);
    SessionProvider sessionProvider = sessionProviderService.getSystemSessionProvider(null);
    Session session = sessionProvider.getSession(this.ws, repository);

    return session;
  }
}
