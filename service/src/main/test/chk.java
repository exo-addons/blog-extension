import org.exoplatform.com.blog.service.IBlogService;
import org.exoplatform.com.blog.service.impl.BlogServiceImpl;

/**
 * Created by toannh on 8/5/14.
 */
public class chk {

  public static void main(String[] args) {
    IBlogService blogService = new BlogServiceImpl(null,null);
    blogService.getBlogArchive().getCount("2013", "1");
  }
}
