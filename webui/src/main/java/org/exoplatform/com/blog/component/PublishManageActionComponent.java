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

import org.exoplatform.ecm.webui.component.explorer.UIJCRExplorer;
import org.exoplatform.ecm.webui.component.explorer.control.listener.UIActionBarActionListener;
import org.exoplatform.services.ecm.publication.PublicationService;
import org.exoplatform.services.wcm.utils.WCMCoreUtils;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 * exo@exoplatform.com
 * Aug 18, 2014
 */

@ComponentConfig(
        events = {@EventConfig(listeners = PublishManageActionComponent.UnpublishActionListener.class)})
public class PublishManageActionComponent extends UIComponent {

  public static final String NON_PUBLISHED = "non published";
  public static final String PUBLISHED = "published";

  public void showMsg(String msg) {
    String msgLog = msg + "";
    UIApplication uiApp = getAncestorOfType(UIApplication.class);
    Object[] arg = {""};
    uiApp.addMessage(new ApplicationMessage(msgLog, arg,
            ApplicationMessage.INFO));
  }

  public static class UnpublishActionListener extends UIActionBarActionListener<PublishManageActionComponent> {
    @Override
    protected void processEvent(Event<PublishManageActionComponent> event) throws Exception {
      PublishManageActionComponent demoActionComponent = event.getSource();

      PublicationService publicationService = WCMCoreUtils.getService(PublicationService.class);
      UIJCRExplorer uiExplorer = demoActionComponent.getAncestorOfType(UIJCRExplorer.class);
      Node node = uiExplorer.getCurrentNode();
      publicationService.unsubcribeLifecycle(node);
      String title="";
      if(node.hasProperty("exo:title")){
        try {
          title = node.getProperty("exo:title").getString();
        }catch(RepositoryException ex){}
      }
      demoActionComponent.showMsg(title+ " have been unpublished!");
    }
  }
}
