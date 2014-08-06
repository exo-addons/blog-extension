package org.exoplatform.com.blog.service.action;

import org.apache.commons.chain.Context;
import org.exoplatform.com.blog.service.IBlogService;
import org.exoplatform.com.blog.service.impl.BaseService;
import org.exoplatform.services.cms.link.LinkManager;
import org.exoplatform.services.cms.link.impl.LinkManagerImpl;
import org.exoplatform.services.command.action.Action;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.wcm.utils.WCMCoreUtils;

import javax.jcr.Node;

/**
 * Created by toannh on 8/6/14.
 */
public class RemoveBlogAction extends BaseService implements Action {
  private Log log = ExoLogger.getExoLogger(AddBlogAction.class);
  ;

  @Override
  public boolean execute(Context context) throws Exception {
    IBlogService blogService = WCMCoreUtils.getService(IBlogService.class);
    Node node = (Node) context.get("currentItem");
    LinkManager linkManager = new LinkManagerImpl(sessionProviderService);
    Node blogNode = linkManager.getTarget(node);
    blogService.removeBlog(blogNode);
    return false;
  }
}
