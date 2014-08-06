import org.exoplatform.com.blog.service.IBlogService;
import org.exoplatform.services.deployment.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by toannh on 8/6/14.
 */
public class TestBlogService {
  IBlogService blogService = Utils.getService(IBlogService.class);
  void TestBlogService(){

  }
  public void testBlogArchive(){
  }

  public static void main(String[] args) {
    HashMap<String, HashMap> selects = new HashMap<String, HashMap>();

    for(Map.Entry<String, HashMap> entry : selects.entrySet()) {
      String key = entry.getKey();
      HashMap value = entry.getValue();

      // do what you have to do here
      // In your case, an other loop.
    }

    List<Integer> lst = new ArrayList<Integer>();

  }
}