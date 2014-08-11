package org.exoplatform.blog;

import org.exoplatform.com.blog.service.BlogService;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 7, 2014
 */
public class TestBlogService extends TestBlog {
  private static BlogService blogService;

  static {

  }

  public void setUp() throws Exception {
    super.setUp();
//    System.out.println("----------------------------INIT-----------------------------");
////    2014
//    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
//    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
//    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
//    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
//    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
//
//    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
//    addBlog("Post-002", "Post-002 Title", "Post-002 Summary", new GregorianCalendar(2014, 02, 01));
//    addBlog("Post-002", "Post-002 Title", "Post-002 Summary", new GregorianCalendar(2014, 02, 01));
//    addBlog("Post-002", "Post-002 Title", "Post-002 Summary", new GregorianCalendar(2014, 02, 01));
//    addBlog("Post-002", "Post-002 Title", "Post-002 Summary", new GregorianCalendar(2014, 02, 01));
//
//    addBlog("Post-004", "Post-004 Title", "Post-004 Summary", new GregorianCalendar(2014, 04, 01));
////    2013
//    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 01, 01));
//    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 01, 01));
//    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 01, 01));
//    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 01, 01));
//    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 01, 01));
//
//    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 02, 01));
//    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 02, 01));
//    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 02, 01));
//    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 03, 01));
//    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 03, 01));
//
//    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 04, 01));
//    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 04, 01));
//    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 04, 01));
//    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 04, 01));
//    System.out.println("---added init data");
//
//    blogService = WCMCoreUtils.getService(BlogService.class);
  }

//  public void testGetYearArchives(){
//    System.out.println("-----------------Year Archive----------------");
//    List<Integer> years = blogService.getArchiveYears();
//    // return 2 year: 2013, 2014
//    System.out.println("Total: "+years.size());
//    for(int year:years){
//      System.out.println(year);
//    }
//    assertEquals("Test get year failed", 2, years.size());
//  }
//
//  public void testGetMonth(){
//    //get 2014 --> 3 months: 01, 02, 03
//    List<Integer> months = blogService.getArchiveMonths(2014);
//    System.out.println("-----------------Month Archive----------------");
//    System.out.println("Total: "+months.size());
//    for(int month: months){
//      System.out.println(month);
//    }
//    assertEquals("Test get month failed ", 3, months.size());
//  }
//  public void testBlogArchive(){
//    List<Integer> years = blogService.getArchiveYears();
//    System.out.println("-------------------Archive----------------");
//    // print blog-archive cached table;
//    for(Integer year: years){
//      System.out.println(year +" ("+blogService.getArchivesCountInYear(year)+")");
//      List<Integer> months = blogService.getArchiveMonths(year);
//      for(Integer month: months){
//        System.out.println(Util.numberToWord(month) + " ("+blogService.getArchivesCountInMonth(year, month)+")");
//      }
//    }
//  }
//
//  public void testGetBlog(){
//    int year = 2014;
//    int month=2;
//    // Time: 2014/02 -->return 4 posts
//    List<Node> nodes = blogService.getBlogs(year, month);
//    System.out.println("--------------------------Nodes By Year/Month "+year+"/"+month+" size: "+nodes.size());
//    for(Node node : nodes){
//      String _name = "";
//      try {
//        _name = node.getProperty("exo:title").getString();
//      }catch(Exception ex){
//        log.error(ex.getMessage());
//      }
//      System.out.println("name: "+_name);
//    }
//    assertEquals("Get blog time 2014/02 failed", 4, nodes.size());
//  }
//
//  public void testAddBlog() throws Exception{
//    //total blog of 2014/08 before create a new post
//    int postCountBefore = blogService.getArchivesCountInMonth(2014,7);
//    //add 5 post
//    addBlog("Post-000-2014", "Post-2014 Title", "Post-2014 Summary", new GregorianCalendar(2014, 07, 12));
//    addBlog("Post-001-2014", "Post-2014 Title", "Post-2014 Summary", new GregorianCalendar(2014, 07, 12));
//    addBlog("Post-002-2014", "Post-2014 Title", "Post-2014 Summary", new GregorianCalendar(2014, 07, 12));
//    addBlog("Post-003-2014", "Post-2014 Title", "Post-2014 Summary", new GregorianCalendar(2014, 07, 12));
//    addBlog("Post-004-2014", "Post-2014 Title", "Post-2014 Summary", new GregorianCalendar(2014, 07, 12));
//
//
//    int postCountAfter = blogService.getArchivesCountInMonth(2014,7);
//    int denta = postCountAfter - postCountBefore;
//    assertTrue("Increate blog cached table", denta == 5);
//  }
//
//  public void testRemoveBlog() throws Exception{
//
//  }
//
//  public Node addBlog(String name, String title, String summary, Calendar date) throws Exception{
//    Session session = getSession();
//    Node node = session.getRootNode().addNode(name, BLOG_NODE);
//
//    node.setProperty("exo:title", title);
//    node.setProperty("exo:summary", summary);
//    node.setProperty("exo:dateCreated", date);
//    session.save();
//    return node;
//  }
//
//  public void testGetArchivesCountInYear(){
//    //2013 --> return 14 post
//    int yearPostTotal = blogService.getArchivesCountInYear(2013);
//    assertEquals("Count post by year failed ", 14 , yearPostTotal);
//  }
//
//  public void testGetArchivesCountInMonth(){
//    //2013/01 --> return 5 post
//    int monthPostTotal = blogService.getArchivesCountInMonth(2013, 01);
//    assertEquals("Count post by year failed ", 5 , monthPostTotal);
//  }


}
