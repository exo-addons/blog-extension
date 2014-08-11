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


package org.exoplatform.com.blog.service.rest;

import org.exoplatform.com.blog.service.BlogService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.security.RolesAllowed;
import javax.jcr.Node;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 7, 2014
 */
@Path("/blog/service")
public class BlogServiceRest implements ResourceContainer {
  private Log log = ExoLogger.getExoLogger(BlogServiceRest.class);

  private BlogService blogService;

  public BlogServiceRest(BlogService blogService) {
    this.blogService = blogService;
  }

  @POST
  @Path("/get-blogs")
  @RolesAllowed("users")
  public Response getBlogs(@QueryParam("year") Integer year, @QueryParam("month") Integer month) {
    if(year==null || month==null) return Response.ok("false").build();
    JSONArray result = new JSONArray();
    List<Node> blogs = blogService.getBlogs(year, month);
    try {
      for (Node node : blogs) {
        JSONObject obj = new JSONObject();
        obj.put("postTitle", node.getProperty("exo:title").getString());
        obj.put("postPath", node.getPath());
        result.put(obj);
      }
      return Response.ok(result.toString(), MediaType.APPLICATION_JSON).build();
    }catch(Exception ex){
      log.error(ex.getMessage());
    }
    return Response.ok("false").build();
  }
}
