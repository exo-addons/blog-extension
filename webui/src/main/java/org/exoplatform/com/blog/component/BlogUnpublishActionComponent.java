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

package org.exoplatform.com.blog.component;

import org.exoplatform.com.blog.component.filter.BlogUnpublishActionFilter;
import org.exoplatform.ecm.webui.component.explorer.UIJCRExplorer;
import org.exoplatform.ecm.webui.component.explorer.control.filter.IsNotLockedFilter;
import org.exoplatform.ecm.webui.component.explorer.control.listener.UIActionBarActionListener;
import org.exoplatform.services.ecm.publication.PublicationService;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.wcm.publication.WCMPublicationService;
import org.exoplatform.services.wcm.utils.WCMCoreUtils;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.ext.filter.UIExtensionFilter;
import org.exoplatform.webui.ext.filter.UIExtensionFilters;

import javax.jcr.Node;
import java.util.Arrays;
import java.util.List;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 * exo@exoplatform.com
 * Aug 18, 2014
 */

@ComponentConfig(
        events = {@EventConfig(listeners = BlogUnpublishActionComponent.UnpublishActionListener.class)})
public class BlogUnpublishActionComponent extends UIComponent {

  public void showMsg(String msg) {
    String msgLog = msg + "";
    UIApplication uiApp = getAncestorOfType(UIApplication.class);
    Object[] arg = {""};
    uiApp.addMessage(new ApplicationMessage(msgLog, arg,
            ApplicationMessage.INFO));
  }

  public static class UnpublishActionListener extends UIActionBarActionListener<BlogUnpublishActionComponent> {
    @Override
    protected void processEvent(Event<BlogUnpublishActionComponent> event) throws Exception {
      BlogUnpublishActionComponent unpublishComponent = event.getSource();

      UIJCRExplorer uiExplorer = unpublishComponent.getAncestorOfType(UIJCRExplorer.class);
      Node currentNode = uiExplorer.getCurrentNode();

      PublicationService publicationService = WCMCoreUtils.getService(PublicationService.class);
      WCMPublicationService wcmPublicationService = WCMCoreUtils.getService(WCMPublicationService.class);
      Identity identity = ConversationState.getCurrent().getIdentity();
      String lifeCycleName = publicationService.getNodeLifecycleName(currentNode);
      wcmPublicationService.unsubcribeLifecycle(currentNode);
      wcmPublicationService.enrollNodeInLifecycle(currentNode, lifeCycleName, identity.getUserId());

      String title = "";
      if (currentNode.hasProperty("exo:title")) {
        title = currentNode.getProperty("exo:title").getString();
        unpublishComponent.showMsg(title + " have been unpublished!");
      } else {
        unpublishComponent.showMsg(title + " couldn't unpublished!");
      }
    }
  }

  private static final List<UIExtensionFilter> FILTERS = Arrays.asList(
          new UIExtensionFilter[]{
                  new IsNotLockedFilter(), new BlogUnpublishActionFilter()});

  @UIExtensionFilters
  public List<UIExtensionFilter> getFilters() {
    return FILTERS;
  }
}
