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



package org.exoplatform.com.blog.service.action;

import org.apache.commons.chain.Context;
import org.exoplatform.com.blog.service.BlogService;
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
    BlogService blogService = WCMCoreUtils.getService(BlogService.class);
    Node node = (Node) context.get("currentItem");
    blogService.addPost(node);
    return false;
  }

}
