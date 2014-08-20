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
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.cms.voting.VotingService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
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

  private BlogService blogService;
  private VotingService votingService;

  public BlogServiceRest(BlogService blogService, VotingService votingService) {
    this.blogService = blogService;
    this.votingService = votingService;
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

    UserACL userACL = WCMCoreUtils.getService(UserACL.class);
    Identity identity = ConversationState.getCurrent().getIdentity();

    boolean isAdmin = userACL.isUserInGroup(userACL.getAdminGroups());
    String viewer = identity.getUserId();

    String postPath = data.getFirst("postPath");
    String nodePath = data.getFirst("nodePath");

    Node postNode = blogService.getNode(postPath);

    try {
      if (postNode != null && postNode.hasProperty("exo:owner")) {
        String postOwner = postNode.getProperty("exo:owner").getString();
        if (!(isAdmin || postOwner.equals(viewer))) {
          return Response.ok("Permission denied !", MediaType.APPLICATION_JSON).build();
        }
      }
      //continue
    } catch (RepositoryException ex) {
      if (log.isErrorEnabled()) log.error(ex.getMessage());
      return Response.ok("Post is not exits!", MediaType.APPLICATION_JSON).build();
    }

    Node rs = blogService.changeStatus(nodePath);
    JSONObject obj = new JSONObject();
    try {
      try {
        if (rs.hasProperty("exo:blogStatus")) {
          obj.put("result", rs.getProperty("exo:blogStatus").getBoolean());
        } else {
          obj.put("result", -1);
        }
        return Response.ok(obj.toString(), MediaType.APPLICATION_JSON).build();
      } catch (RepositoryException ex) {
        if (log.isErrorEnabled()) log.error(ex.getMessage());
      }
      obj.put("result", -1);
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
    Identity identity = ConversationState.getCurrent().getIdentity();
    Node nodeToVote = blogService.getNode(postPath);
    try {
      votingService.vote(nodeToVote, score, identity.getUserId(), "");
      return Response.ok("{\"result\": true}", MediaType.TEXT_PLAIN).build();
    } catch (Exception ex) {
      if (log.isErrorEnabled()) log.error(ex.getMessage());
    }
    return Response.ok("Failed", MediaType.TEXT_PLAIN).build();
  }

  @POST
  @Path("/editComment")
  @RolesAllowed("users")
  public Response editComment(MultivaluedMap<String, String> data) {
    UserACL userACL = WCMCoreUtils.getService(UserACL.class);
    Identity identity = ConversationState.getCurrent().getIdentity();

    boolean isAdmin = userACL.isUserInGroup(userACL.getAdminGroups());
    String viewer = identity.getUserId();

    String commentPath = data.getFirst("commentPath");
    String newComment = data.getFirst("newComment");
    Node nodeEdit = blogService.getNode(commentPath);
    JSONObject obj = new JSONObject();
    try {
      if (nodeEdit != null && nodeEdit.hasProperty("exo:commentor")) {
        if (!(isAdmin || viewer.equals(nodeEdit.getProperty("exo:commentor").getString()))) {
          return Response.ok("Permission denied!", MediaType.TEXT_PLAIN).build();
        }
        boolean result = blogService.editComment(nodeEdit, newComment);
        obj.put("result", result);
        return Response.ok(obj.toString(), MediaType.APPLICATION_JSON).build();
      }
    } catch (RepositoryException ex) {
      if (log.isErrorEnabled()) {
        log.error(ex.getMessage());
      }
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
    UserACL userACL = WCMCoreUtils.getService(UserACL.class);
    Identity identity = ConversationState.getCurrent().getIdentity();

    boolean isAdmin = userACL.isUserInGroup(userACL.getAdminGroups());
    String viewer = identity.getUserId();

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
    Node node = blogService.getNode(nodePath);
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
