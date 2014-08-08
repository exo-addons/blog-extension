package org.exoplatform.com.blog.service.impl;

import org.exoplatform.com.blog.service.IBlogService;
import org.exoplatform.com.blog.service.entity.BlogArchive;
import org.exoplatform.com.blog.service.util.Util;
import org.exoplatform.services.cms.drives.DriveData;
import org.exoplatform.services.cms.drives.ManageDriveService;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.wcm.utils.WCMCoreUtils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.util.*;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 * exo@exoplatform.com
 * Aug 4, 2014
 */
public class BlogServiceImpl implements IBlogService {

  private boolean initData = true;
  public static String BLOG_NODE = "exo:blog";
  Log log = ExoLogger.getExoLogger(BlogServiceImpl.class);

  private String repo = "repository";
  private String ws = "collaboration";

  private RepositoryService repoService;
  private SessionProviderService sessionProviderService;
  private ManageDriveService manageDriveService = WCMCoreUtils.getService(ManageDriveService.class);

  private Map<Integer, BlogArchive> blogArchive = new HashMap<Integer, BlogArchive>();
  private Map<Integer, Integer> month = new HashMap<Integer, Integer>();

  public void add(Integer year, Integer month) {
    BlogArchive blogArchive;
    if (!this.blogArchive.containsKey(year)) { // year doesnt exits
      this.month = new HashMap<Integer, Integer>();
      blogArchive = new BlogArchive();
      blogArchive.setYear_post(1);
      this.month.put((Integer) month, 1);
      blogArchive.setMonth(this.month);
      this.blogArchive.put(year, blogArchive);
    } else { // year exits
      blogArchive = this.blogArchive.get(year);
      blogArchive.setYear_post(blogArchive.getYear_post() + 1);
      this.month = blogArchive.getMonth();
      if (!this.month.containsKey(month)) { //month of year doesnt exits
        this.month.put((Integer) month, 1);
      } else {
        this.month.put((Integer) month, this.month.get(month) + 1);
      }
      blogArchive.setMonth(this.month);
      this.blogArchive.put(year, blogArchive);
    }
  }

  public BlogServiceImpl(RepositoryService repoService, SessionProviderService sessionProviderService) {
    this.repoService = repoService;
    this.sessionProviderService = sessionProviderService;
    try {
      this.repo = repoService.getCurrentRepository().getConfiguration().getName();
      this.ws = repoService.getCurrentRepository().getConfiguration().getDefaultWorkspaceName();
    } catch (Exception ex) {
      log.error(ex.getMessage());
      log.info("Using default repository & workspace");
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
          add(_year, _month);
        }
      } catch (Exception ex) {
        log.error(ex.getMessage());
      }
    }
  }

  public boolean isInitData() {
    return initData;
  }

  public void setInitData(boolean initData) {
    this.initData = initData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Integer> getArchiveYears() {
//    List<Integer> result = new ArrayList<Integer>();
//    HashMap<Integer, BlogArchive> blogArchiveHashMap = this.blogArchive;
//    for (Map.Entry<Integer, BlogArchive> entry : blogArchiveHashMap.entrySet()) {
//      result.add(entry.getKey());
//    }
//    blogArchive.keySet()
    return new ArrayList(blogArchive.keySet());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Integer> getArchiveMonths(int year) {
    BlogArchive month = this.blogArchive.get(year);
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
    return this.blogArchive.get(year).getYear_post();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int getArchivesCountInMonth(int year, int month) {
    return this.blogArchive.get(year).getMonth().get(month);
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
        BlogArchive _blogYearArchive = blogArchive.get(year);
        blogArchive.get(year).setYear_post(_blogYearArchive.getYear_post() + 1);
        Map<Integer, Integer> monthByYear = _blogYearArchive.getMonth();

        if (monthByYear.containsKey(month)) {
          blogArchive.get(year).getMonth().put(month, monthByYear.get(month) + 1);
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
        BlogArchive _blogYearArchive = blogArchive.get(year);
        blogArchive.get(year).setYear_post(_blogYearArchive.getYear_post() - 1);
        Map<Integer, Integer> monthByYear = _blogYearArchive.getMonth();

        if (monthByYear.containsKey(month)) {
          blogArchive.get(year).getMonth().put(month, monthByYear.get(month) - 1);
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
    DriveData driveData = manageDriveService.getDriveByName("Blog");
    List<Node> rs = new ArrayList<Node>();
    String searchPath = driveData.getHomePath();
    searchPath = searchPath.substring(0, searchPath.lastIndexOf("/") + 1);
    searchPath += "%";
    StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ").append(nodeElement);
    queryBuilder.append(" WHERE jcr:path LIKE '" + searchPath + "' ");
    queryBuilder.append(" AND   exo:dateCreated >= TIMESTAMP '" + firstDayOfMonth + "' ");
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
