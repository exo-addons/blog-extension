package org.exoplatform.com.blog.service.action;

import org.apache.commons.chain.Context;
import org.exoplatform.com.blog.service.IBlogService;
import org.exoplatform.services.command.action.Action;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.wcm.utils.WCMCoreUtils;

import javax.jcr.Node;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 6, 2014
 */
public class AddBlogAction implements Action {
  private Log log = ExoLogger.getExoLogger(AddBlogAction.class);

  @Override
  public boolean execute(Context context) throws Exception {
    IBlogService blogService = WCMCoreUtils.getService(IBlogService.class);
    Node node = (Node) context.get("currentItem");
    blogService.addBlog(node);
    return false;
  }

}
