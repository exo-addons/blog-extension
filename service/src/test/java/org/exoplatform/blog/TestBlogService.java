/*
 * Copyright (C) 2003-2014 eXo Platform SEA.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.blog;
import junit.framework.TestCase;
import org.exoplatform.com.blog.service.BlogService;
import org.exoplatform.com.blog.service.util.Util;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.cms.comments.CommentsService;
import org.exoplatform.services.ecm.publication.NotInPublicationLifecycleException;
import org.exoplatform.services.ecm.publication.PublicationPlugin;
import org.exoplatform.services.ecm.publication.PublicationService;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityConstants;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 * exo@exoplatform.com
 * Aug 7, 2014
 */
public class TestBlogService extends TestCase {
  private Log log = ExoLogger.getExoLogger(TestBlogService.class);

  private final static String COMMENTOR_MESSAGES = "exo:commentContent";
  private final static String I18NMixin = "mix:i18n";
  private static final String CURRENT_STATE = "publication:currentState";
  private static final String ENROLLED = "enrolled";
  private static final String PUBLISHED = "published";
  private static final String BLOG_NODE = "exo:blog";

  private static StandaloneContainer container;
  private static BlogService blogService;
  private static CommentsService commentsService;
  private static PublicationService publicationService;
  private PublicationPlugin plugin_;


  static {
    initContainer();
  }

  /**
   * Set current container
   */
  private void begin() {
    RequestLifeCycle.begin(container);
  }

  /**
   * Clear current container
   */
  protected void tearDown() throws Exception {
    RequestLifeCycle.end();
  }

  private static void initContainer() {
    try {
      String containerConf = Thread.currentThread()
              .getContextClassLoader()
              .getResource("conf/standalone/configuration.xml")
              .toString();
      StandaloneContainer.addConfigurationURL(containerConf);
      String loginConf = Thread.currentThread().getContextClassLoader().getResource("conf/standalone/login.conf").toString();
      System.setProperty("java.security.auth.login.config", loginConf);
      container = StandaloneContainer.getInstance();
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize standalone container: " + e.getMessage(), e);
    }
  }

  @Override
  protected void setUp() throws Exception {
    begin();
    Identity systemIdentity = new Identity(IdentityConstants.SYSTEM);
    ConversationState.setCurrent(new ConversationState(systemIdentity));
    reset();
    init();
    blogService = (BlogService) container.getComponentInstanceOfType(BlogService.class);
    commentsService = (CommentsService) container.getComponentInstanceOfType(CommentsService.class);
    publicationService = (PublicationService) container.getComponentInstanceOfType(PublicationService.class);

//    plugin_ = new org.exoplatform.services.wcm.publication.DumpPublicationPlugin();
//    plugin_.setName("Simple");
//    plugin_.setDescription("Simple");
//    publicationService.addPublicationPlugin(plugin_);
  }

  public void testGetYearArchives() {
    System.out.println("-----TEST GET YEAR-----");
    printBlogArchive();
    List<Integer> years = blogService.getArchiveYears();
    // return 2 year: 2013, 2014
    System.out.println("testGetYearArchives() YEAR: " + years.size());
    for (int year : years) {
      System.out.println(year);
    }
    assertEquals("Test get year failed", 2, years.size());
  }

  public void testGetMonth() {
    System.out.println("-----TEST GET MONTH-----");
    printBlogArchive();
    //get 2014 --> 3 months: 01, 02, 03
    List<Integer> months = blogService.getArchiveMonths(2014);
    printBlogArchive();
    System.out.println("testGetMonth() Month of 2014: " + months.size());
    for (int month : months) {
      System.out.println("---" + Util.numberToWord(month));
    }
    assertEquals("Test get month failed ", 3, months.size());
  }

  public void testGetBlog() throws Exception{
    System.out.println("-----TEST GET POST - 01/2014-----");
    int year = 2014;
    int month = 0;
    // Time: 2014/02 -->return 6 posts
    List<Node> nodes = blogService.getBlogs(year, month);
    printBlogArchive();
    System.out.println("testGetBlog() Nodes By Year/Month: 2014/02: " + nodes.size() );
    for (Node node : nodes) {
      String _name = "";
      try {
        _name = node.getProperty("exo:title").getString();
      } catch (Exception ex) {
        log.error(ex.getMessage());
      }
      System.out.println("name: " + _name+", path: "+node.getPath());
    }
    assertEquals("Get blog time 2014/02 failed", 6, nodes.size());
  }

  public void testGetArchivesCountInYear() {
    System.out.println("-----TEST GET COUNT IN YEAR - 2013-----");
    //2013 --> return 14 post
    printBlogArchive();
    int yearPostTotal = blogService.getArchivesCountInYear(2013);
    System.out.println("testGetArchivesCountInYear(2013): " + yearPostTotal);
    assertEquals("Count post by year failed ", 14, yearPostTotal);
  }

  public void testGetArchivesCountInMonth() {
    System.out.println("-----TEST GET COUNT IN MONTH - 01/2013-----");
    //2013/01 --> return 5 post
    printBlogArchive();
    int monthPostTotal = blogService.getArchivesCountInMonth(2013, 01);
    System.out.println("testGetArchivesCountInMonth(2013, 02): " + monthPostTotal);
    assertEquals("Count post by year failed ", 5, monthPostTotal);
  }

  public void testIncreasePostView() throws Exception {
    System.out.println("-----TEST INCREASE POST VIEW-----");
    Session session = getSession();
    Node rootNode = session.getRootNode();
    Node blog = (rootNode.hasNode("Blog")) ? rootNode.getNode("Blog") : rootNode.addNode("Blog");
    Node node = blog.getNode("Post-001");

    long beforeIncrease = blogService.getPostViewCount(node);
    blogService.increasePostView(node);
    long afterIncrease = blogService.getPostViewCount(node);

    long denta = afterIncrease-beforeIncrease;
    assertTrue("Test Increase failed", denta == 1 );
  }

  public void testGetPostView() throws Exception{
    System.out.println("-----TEST GET POST VIEW COUNT-----");
    Session session = getSession();
    Node rootNode = session.getRootNode();
    Node blog = (rootNode.hasNode("Blog")) ? rootNode.getNode("Blog") : rootNode.addNode("Blog");
    Node node = blog.getNode("Post-001");

    long postViewCount = blogService.getPostViewCount(node);

    assertTrue("Test getPostView failed", postViewCount != -1 );
  }

  public void testAddBlog() throws Exception {
    //total blog of 2014/08 before create a new post
    int postCountBefore = blogService.getArchivesCountInMonth(2014, 7);
    //add 5 post
    printBlogArchive();
    System.out.println("-----------------------------testAddBlog--------------------------------");
    Node node1 = addBlog("Post-000-2014", "Post-2014 Title", "Post-2014 Summary", new GregorianCalendar(2014, 8, 1));
    Node node2 = addBlog("Post-001-2014", "Post-2014 Title", "Post-2014 Summary", new GregorianCalendar(2014, 8, 1));
    Node node3 = addBlog("Post-002-2014", "Post-2014 Title", "Post-2014 Summary", new GregorianCalendar(2014, 8, 1));
    Node node4 = addBlog("Post-003-2014", "Post-2014 Title", "Post-2014 Summary", new GregorianCalendar(2014, 8, 1));
    Node node5 = addBlog("Post-004-2014", "Post-2014 Title", "Post-2014 Summary", new GregorianCalendar(2014, 8, 1));

    blogService.addBlog(node1);
    blogService.addBlog(node2);
    blogService.addBlog(node3);
    blogService.addBlog(node4);
    blogService.addBlog(node5);
    printBlogArchive();
    int postCountAfter = blogService.getArchivesCountInMonth(2014, 7);

    int denta = postCountAfter - postCountBefore;
    assertTrue("Increate blog cached table", denta == 5);
  }

  public void testRemoveBlog() throws Exception {
    System.out.println("--------------------testRemoveBlog--------------------");
//    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
    printBlogArchive();
    int monthPostTotalBefore = blogService.getArchivesCountInMonth(2013, 01);
    Session session = getSession();
    Node rootNode = session.getRootNode();
    Node blog = (rootNode.hasNode("Blog")) ? rootNode.getNode("Blog") : rootNode.addNode("Blog");
    //("Post-000-2013001", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 01, 01));
    Node node = blog.getNode("Post-000-2013001");
    blogService.removeBlog(node);
    int monthPostTotalAfter = blogService.getArchivesCountInMonth(2013, 01);
    int denta = monthPostTotalBefore - monthPostTotalAfter;

    assertTrue("Test remove blog failed ", (denta == 1));
    printBlogArchive();
  }

  public void testChangeStatus() throws Exception{
    System.out.println("-----TEST CHANGE STATUS-----");
    Session session = getSession();
    Node rootNode = session.getRootNode();
    Node blog = (rootNode.hasNode("Blog")) ? rootNode.getNode("Blog") : rootNode.addNode("Blog");
    Node node = blog.getNode("Post-001");
    boolean before = false;
    if(node.hasProperty("exo:blogStatus")) {
      before = node.getProperty("exo:blogStatus").getBoolean();
    }
    boolean after = blogService.changeStatus(null, node.getPath());

    assertTrue("Test change status failed", !(before && after));
  }

  public void testEditComment() throws Exception{
    System.out.println("---TEST EDIT COMMENT---");
    Session session = getSession();
    Node rootNode = session.getRootNode();
    Node blog = (rootNode.hasNode("Blog")) ? rootNode.getNode("Blog") : rootNode.addNode("Blog");
    Node node = blog.getNode("Post-001");
    if(node.canAddMixin(I18NMixin)){
      node.addMixin(I18NMixin);
    }

    commentsService.addComment(node, "toannh", "toannh@explatform.com", null, "1st comment!", "en");
    List<Node> nodes = commentsService.getComments(node, "en");
    commentsService.updateComment(nodes.get(0), "updated");
    nodes = commentsService.getComments(node, "en");
    assertEquals("updated", nodes.get(0).getProperty(COMMENTOR_MESSAGES).getString());
  }

  public void testUnpulish() throws Exception{
    Exception e = null;
    System.out.println("---TEST UnPublish---");
    Session session = getSession();
    Node rootNode = session.getRootNode();
    Node blog = (rootNode.hasNode("Blog")) ? rootNode.getNode("Blog") : rootNode.addNode("Blog");
    Node node = blog.getNode("Post-001");
    try {
      publicationService.unsubcribeLifecycle(node);
    } catch (NotInPublicationLifecycleException ex) {
      e = ex;
    }
    assertNotNull(e);

    node.addMixin("exo:sortable");
    session.save();
    publicationService.enrollNodeInLifecycle(node, plugin_.getLifecycleName());
    publicationService.unsubcribeLifecycle(node);
//    assertFalse(node.isNodeType(DumpPublicationPlugin.PUBLICATION));
  }

  public void printBlogArchive() {
    List<Integer> years = blogService.getArchiveYears();
    // print blog-archive cached table;
    for (Integer year : years) {
      System.out.println(year + " (" + blogService.getArchivesCountInYear(year) + ")");
      List<Integer> months = blogService.getArchiveMonths(year);
      for (Integer month : months) {
        System.out.println("---" + Util.numberToWord(month) + " (" + blogService.getArchivesCountInMonth(year, month) + ")");
      }
    }
  }
  public Node addBlog(String name, String title, String summary, Calendar date) throws Exception {
    Session session = getSession();
    Node rootNode = session.getRootNode();
    Node blog = (rootNode.hasNode("Blog")) ? rootNode.getNode("Blog") : rootNode.addNode("Blog");
    Node node = blog.addNode(name, BLOG_NODE);

    node.setProperty("exo:title", title);
    node.setProperty("exo:blogSummary", summary);
    node.setProperty("exo:dateCreated", date);
    session.save();
    return node;
  }

//  public void testSession(){
//    HttpSession httpSession = org.exoplatform.portal.webui.util.Util.getPortalRequestContext().getRequest().getSession();
////    httpSession.setAttribute("nodeName");
//    httpSession.getAttribute("nodeName");
//  }
  private void reset() throws Exception {
    Session session = getSession();
    Node rootNode = session.getRootNode();
    Node blog = (rootNode.hasNode("Blog")) ? rootNode.getNode("Blog") : rootNode.addNode("Blog");
    blog.remove();
    rootNode.save();
  }

  protected Session getSession() throws Exception {
    RepositoryService repoService = (RepositoryService) container.getComponentInstanceOfType(RepositoryService.class);
    return repoService.getCurrentRepository().login();
  }

  private void init() throws Exception {
    System.out.println("---DUMP DATA TEST---");
//    2014
    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));

    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
    addBlog("Post-002", "Post-002 Title", "Post-002 Summary", new GregorianCalendar(2014, 02, 02));
    addBlog("Post-002", "Post-002 Title", "Post-002 Summary", new GregorianCalendar(2014, 02, 02));
    addBlog("Post-002", "Post-002 Title", "Post-002 Summary", new GregorianCalendar(2014, 02, 02));
    addBlog("Post-002", "Post-002 Title", "Post-002 Summary", new GregorianCalendar(2014, 02, 02));

    addBlog("Post-004", "Post-004 Title", "Post-004 Summary", new GregorianCalendar(2014, 04, 04));
//    2013
    addBlog("Post-000-2013001", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 01, 01));
    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 01, 01));
    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 01, 01));
    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 01, 01));
    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 01, 01));

    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 02, 01));
    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 02, 01));
    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 02, 01));
    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 03, 01));
    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 03, 01));

    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 04, 01));
    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 04, 01));
    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 04, 01));
    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 04, 01));
  }
}
