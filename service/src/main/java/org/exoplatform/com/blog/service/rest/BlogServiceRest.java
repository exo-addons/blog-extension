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
import org.exoplatform.com.blog.service.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.wcm.utils.WCMCoreUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.security.RolesAllowed;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 * exo@exoplatform.com
 * Aug 7, 2014
 */
@Path("/blog/service")
public class BlogServiceRest implements ResourceContainer {
  private Log log = ExoLogger.getExoLogger(BlogServiceRest.class);

  private BlogService blogService = WCMCoreUtils.getService(BlogService.class);

  public BlogServiceRest(){
    }

  @POST
  @Path("/get-blogs")
  @RolesAllowed("users")
  public Response getBlogs(@QueryParam("year") Integer year, @QueryParam("month") Integer month) {
    if (year == null || month == null) return Response.ok("false").build();
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
    } catch (Exception ex) {
      log.error(ex.getMessage());
    }
    return Response.ok("false").build();
  }

  @POST
  @Path("/changeStatus")
  @RolesAllowed("users")
  public Response getBlogs(MultivaluedMap<String, String> data) {
    String postPath = data.getFirst("postPath");
    String nodePath = data.getFirst("nodePath");

    boolean rs = blogService.changeStatus(postPath, nodePath);
    JSONObject obj = new JSONObject();
    try {
      obj.put("result", rs);
    } catch (JSONException e) {
      if (log.isErrorEnabled()) log.error(e.getMessage());
    }
    return Response.ok(obj.toString(), MediaType.APPLICATION_JSON).build();
  }

  @POST
  @Path("/updateVote")
  @RolesAllowed("users")
  public Response updateVote(MultivaluedMap<String, String> data) {
    String postPath = data.getFirst("postPath");
    double score = Util.getDouble(data.getFirst("score"), 0);
    boolean result = blogService.vote(postPath, score);
    return Response.ok("{\"result\": " + result + "}", MediaType.TEXT_PLAIN).build();
  }

  @POST
  @Path("/editComment")
  @RolesAllowed("users")
  public Response editComment(MultivaluedMap<String, String> data) {
    String commentPath = data.getFirst("commentPath");
    String newComment = data.getFirst("newComment");
    JSONObject obj = new JSONObject();
    try {
      boolean result = blogService.editComment(commentPath, newComment);
      obj.put("result", result);
      return Response.ok(obj.toString(), MediaType.APPLICATION_JSON).build();
    } catch (JSONException e) {
      if (log.isErrorEnabled()) {
        log.error(e.getMessage());
      }
    }
    return Response.ok("failed", MediaType.APPLICATION_JSON).build();
  }


  @POST
  @Path("/delComment")
  @RolesAllowed("users")
  public Response delComment(@QueryParam("nodePath") String nodePath) {
    boolean result = blogService.delComment(nodePath);
    JSONObject obj = new JSONObject();
    try {
      obj.put("result", result);
      return Response.ok(obj.toString(), MediaType.APPLICATION_JSON).build();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return Response.ok("failed", MediaType.APPLICATION_JSON).build();
  }

  @POST
  @Path("/getComment")
  @RolesAllowed("users")
  public Response getComment(@QueryParam("nodePath") String nodePath) {
    Node node = blogService.getCommentNode(nodePath);
    JSONObject obj = new JSONObject();
    try {
      if (node != null && node.hasProperty("exo:commentContent")) {
        obj.put("result", true);
        obj.put("commentContent", node.getProperty("exo:commentContent").getString());
        obj.put("commentPath", node.getPath());
        return Response.ok(obj.toString(), MediaType.APPLICATION_JSON).build();
      }
    } catch (JSONException e) {
      e.printStackTrace();
    } catch (RepositoryException ex) {
      if (log.isErrorEnabled()) log.error(ex.getMessage());
    }
    return Response.ok("failed", MediaType.APPLICATION_JSON).build();
  }
}
