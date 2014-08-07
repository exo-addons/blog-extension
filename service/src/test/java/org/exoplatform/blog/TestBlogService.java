package org.exoplatform.blog;

import org.exoplatform.com.blog.service.IBlogService;
import org.exoplatform.com.blog.service.util.BlogArchiveUtil;
import org.exoplatform.com.blog.service.util.Util;
import org.exoplatform.services.wcm.utils.WCMCoreUtils;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by toannh on 8/7/14.
 */
public class TestBlogService extends TestBlog {
  private static IBlogService blogService;

  private static final String BLOG_NODE = "exo:blog";
  private static BlogArchiveUtil<Integer, Integer> blogArchive = new BlogArchiveUtil<Integer, Integer>();


  static {

  }

  public void setUp() throws Exception {
    super.setUp();
    System.out.println("----------------------------INIT-----------------------------");
//    2014
    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
    addBlog("Post-001", "Post-001 Title", "Post-001 Summary", new GregorianCalendar(2014, 01, 01));
    addBlog("Post-002", "Post-002 Title", "Post-002 Summary", new GregorianCalendar(2014, 02, 01));
    addBlog("Post-002", "Post-002 Title", "Post-002 Summary", new GregorianCalendar(2014, 02, 01));
    addBlog("Post-002", "Post-002 Title", "Post-002 Summary", new GregorianCalendar(2014, 02, 01));
    addBlog("Post-002", "Post-002 Title", "Post-002 Summary", new GregorianCalendar(2014, 02, 01));
    addBlog("Post-004", "Post-004 Title", "Post-004 Summary", new GregorianCalendar(2014, 04, 01));
//    2013
    addBlog("Post-000-2013", "Post-2013 Title", "Post-2013 Summary", new GregorianCalendar(2013, 01, 01));
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
    System.out.println("---added init data");

    blogService = WCMCoreUtils.getService(IBlogService.class);
  }

  public void testBlogArchive(){
    List<Integer> years = blogService.getArchiveYears();
    System.out.println("-------------------Archive----------------");
    for(Integer year: years){
      System.out.println(year +" ("+blogService.getArchivesCountInYear(year)+")");
      List<Integer> months = blogService.getArchiveMonths(year);
      for(Integer month: months){
        System.out.println(Util.numberToWord(month) + " ("+blogService.getArchivesCountInMonth(year, month)+")");
      }
    }
  }

  public void testGetBlog(){
    int year = 2014;
    int month=2;
    List<Node> nodes = blogService.getBlogs(year, month);
    System.out.println("--------------------------Nodes By Year/Month "+year+"/"+month+" size: "+nodes.size());
    for(Node node : nodes){
      String _name = "";
      try {
        _name = node.getProperty("exo:title").getString();
      }catch(Exception ex){
        ex.printStackTrace();
      }
      System.out.println("name: "+_name);
    }
  }


  public void addBlog(String name, String title, String summary, Calendar date) throws Exception{
    Session session = getSession();
    Node node = session.getRootNode().addNode(name, BLOG_NODE);

    node.setProperty("exo:title", title);
    node.setProperty("exo:summary", summary);
    node.setProperty("exo:dateCreated", date);
    session.save();
  }







}
