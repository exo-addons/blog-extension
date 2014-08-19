/*
 *
 *  * Copyright (C) 2003-2014 eXo Platform SEA.
 *  *
 *  * This program is free software; you can redistribute it and/or
 *  * modify it under the terms of the GNU Affero General Public License
 *  * as published by the Free Software Foundation; either version 3
 *  * of the License, or (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program; if not, see<http://www.gnu.org/licenses/>.
 *
 */

package org.exoplatform.com.blog.component.filter;

import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.wcm.extensions.publication.PublicationManager;
import org.exoplatform.services.wcm.extensions.publication.lifecycle.impl.LifecyclesConfig;
import org.exoplatform.services.wcm.utils.WCMCoreUtils;
import org.exoplatform.webui.ext.filter.UIExtensionFilter;
import org.exoplatform.webui.ext.filter.UIExtensionFilterType;

import javax.jcr.Node;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 * exo@exoplatform.com
 * Aug 19, 2014
 */

public class UnpublishActionFilter implements UIExtensionFilter {
  private Log log = ExoLogger.getExoLogger(UnpublishActionFilter.class);

  @Override
  public boolean accept(Map<String, Object> context) throws Exception {
    Node currentNode = (Node) context.get(Node.class.getName());
    if (currentNode.hasProperty("publication:currentState") && currentNode.hasProperty("publication:lifecycle")) {
      String currentState = currentNode.getProperty("publication:currentState").getString();
      if ("published".equals(currentState)) {
        String userId;
        try {
          userId = Util.getPortalRequestContext().getRemoteUser();
        } catch (Exception e) {
          userId = currentNode.getSession().getUserID();
        }
        String nodeLifecycle = currentNode.getProperty("publication:lifecycle").getString();
        PublicationManager publicationManager = WCMCoreUtils.getService(PublicationManager.class);
        List<LifecyclesConfig.Lifecycle> lifecycles = publicationManager.getLifecyclesFromUser(userId, "published");
        for (LifecyclesConfig.Lifecycle lifecycle : lifecycles) {
          if (nodeLifecycle.equals(lifecycle.getName())) {
            return true;
          }
        }
      }

    }
    return currentNode.isNodeType("exo:blog");
  }

  @Override
  public void onDeny(Map<String, Object> stringObjectMap) throws Exception {

  }

  @Override
  public UIExtensionFilterType getType() {
    return UIExtensionFilterType.MANDATORY;
  }
}
