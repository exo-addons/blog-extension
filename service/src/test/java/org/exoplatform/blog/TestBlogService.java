package org.exoplatform.blog;

import org.exoplatform.com.blog.service.BlogService;
import org.exoplatform.com.blog.service.util.Util;

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
public class TestBlogService extends TestBlog {
  private static BlogService blogService;

  static {

  }

  public void setUp() throws Exception {
    super.setUp();
    reset();
    System.out.println("----------------------------INIT-----------------------------");
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

//    blogService = WCMCoreUtils.getService(BlogService.class);
    blogService = (BlogService) container.getComponentInstanceOfType(BlogService.class);
  }

  public void testGetYearArchives() {
    printBlogArchive();
    List<Integer> years = blogService.getArchiveYears();
    // return 2 year: 2013, 2014
    System.out.println("testGetYearArchives() YEAR: " + years.size());
    for (int year : years) {
      System.out.println(year);
    }
//    assertEquals("Test get year failed", 2, years.size());
  }

  public void testGetMonth() {
    //get 2014 --> 3 months: 01, 02, 03
    List<Integer> months = blogService.getArchiveMonths(2014);
    printBlogArchive();
    System.out.println("testGetMonth() Month of 2014: " + months.size());
    for (int month : months) {
      System.out.println("---" + Util.numberToWord(month));
    }
    assertEquals("Test get month failed ", 3, months.size());
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

  public void testGetBlog() {
    int year = 2014;
    int month = 1;
    // Time: 2014/02 -->return 6 posts
    List<Node> nodes = blogService.getBlogs(year, month);
    printBlogArchive();
    System.out.println("testGetBlog() Nodes By Year/Month: 2014/02: " + nodes.size());
    for (Node node : nodes) {
      String _name = "";
      try {
        _name = node.getProperty("exo:title").getString();
      } catch (Exception ex) {
        log.error(ex.getMessage());
      }
      System.out.println("name: " + _name);
    }
    assertEquals("Get blog time 2014/02 failed", 6, nodes.size());
  }

  public void testGetArchivesCountInYear() {
    //2013 --> return 14 post
    printBlogArchive();
    int yearPostTotal = blogService.getArchivesCountInYear(2013);
    System.out.println("testGetArchivesCountInYear(2013): " + yearPostTotal);
    assertEquals("Count post by year failed ", 14, yearPostTotal);
  }

  public void testGetArchivesCountInMonth() {
    //2013/01 --> return 5 post
    printBlogArchive();
    int monthPostTotal = blogService.getArchivesCountInMonth(2013, 01);
    System.out.println("testGetArchivesCountInMonth(2013, 02): " + monthPostTotal);
    assertEquals("Count post by year failed ", 5, monthPostTotal);
  }


  public void testAddBlog() throws Exception{
    //total blog of 2014/08 before create a new post
    int postCountBefore = blogService.getArchivesCountInMonth(2014,7);
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
//    blogService = (BlogService) container.getComponentInstanceOfType(BlogService.class);

    printBlogArchive();
    int postCountAfter = blogService.getArchivesCountInMonth(2014,7);

    int denta = postCountAfter - postCountBefore;
    assertTrue("Increate blog cached table", denta == 5);
  }

  public void testRemoveBlog() throws Exception{
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
    int denta = monthPostTotalBefore-monthPostTotalAfter;

    assertTrue("Test remove blog failed ", (denta == 1));
    printBlogArchive();
  }

  public Node addBlog(String name, String title, String summary, Calendar date) throws Exception {
    Session session = getSession();
    Node rootNode = session.getRootNode();
    Node blog = (rootNode.hasNode("Blog")) ? rootNode.getNode("Blog") : rootNode.addNode("Blog");
    Node node = blog.addNode(name, BLOG_NODE);

    node.setProperty("exo:title", title);
    node.setProperty("exo:summary", summary);
    node.setProperty("exo:dateCreated", date);
//    blogService.addBlog(node);
    session.save();
    return node;
  }

  private void reset() throws Exception {
    Session session = getSession();
    Node rootNode = session.getRootNode();
    Node blog = (rootNode.hasNode("Blog")) ? rootNode.getNode("Blog") : rootNode.addNode("Blog");
    blog.remove();
    rootNode.save();
  }
}
