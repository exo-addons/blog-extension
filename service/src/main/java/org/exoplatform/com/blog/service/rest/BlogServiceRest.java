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
import org.exoplatform.services.cms.comments.CommentsService;
import org.exoplatform.services.cms.voting.VotingService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
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
import javax.jcr.Session;
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
 * Blog rest service, provider functions of blog-frontend executes
 */
@Path("/blog/service")
public class BlogServiceRest implements ResourceContainer {
  private Log log = ExoLogger.getExoLogger(BlogServiceRest.class);
  private final String BLOG_DEFAULT_LANGUAGE = "en";
  private static final String BLOG_COMMENT_STATUS_PROPERTY = "exo:commentStatus";
  private BlogService blogService = WCMCoreUtils.getService(BlogService.class);

  public BlogServiceRest() {
  }

  @POST
  @Path("/get-blogs")
  @RolesAllowed("users")
  public Response getBlogs(@QueryParam("year") Integer year, @QueryParam("month") Integer month) {
    if (year == null || month == null) return Response.ok("false").build();
    JSONArray result = new JSONArray();
    List<Node> blogs = blogService.getPosts(year, month);
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
  @Path("/changeCommentStatus")
  @RolesAllowed("users")
  public Response getBlogs(MultivaluedMap<String, String> data) {
    String postPath = data.getFirst("postPath");
    String commentPath = data.getFirst("nodePath");
    String ws = data.getFirst("ws");
    JSONObject obj = new JSONObject();
    try {
      Node commentNode = blogService.changeCommentStatus(getNode(postPath, ws), getNode(commentPath, ws));
      if (commentNode != null && commentNode.hasProperty(BLOG_COMMENT_STATUS_PROPERTY)) {
        boolean status = commentNode.getProperty(BLOG_COMMENT_STATUS_PROPERTY).getBoolean();
        obj.put("result", status);
        return Response.ok(obj.toString(), MediaType.APPLICATION_JSON).build();
      }
      return Response.ok("{\"result\": \"failed\"}", MediaType.TEXT_PLAIN).build();
    } catch (JSONException e) {
      if (log.isErrorEnabled()) log.error(e.getMessage());
    } catch (RepositoryException ex) {
      if (log.isErrorEnabled()) log.error(ex.getMessage());
    }
    return Response.ok("{\"result\": \"failed\"}", MediaType.TEXT_PLAIN).build();
  }

  @POST
  @Path("/updateVote")
  @RolesAllowed("users")
  public Response updateVote(MultivaluedMap<String, String> data) {
    VotingService votingService = WCMCoreUtils.getService(VotingService.class);
    String postPath = data.getFirst("postPath");
    String ws = data.getFirst("ws");
    double score = Util.getDouble(data.getFirst("score"), 0);
    Identity identity = ConversationState.getCurrent().getIdentity();
    JSONObject obj = new JSONObject();
    try {
      Node nodeToVote = getNode(postPath, ws);
      votingService.vote(nodeToVote, score, identity.getUserId(), BLOG_DEFAULT_LANGUAGE);
      obj.put("result", true);
      return Response.ok(obj.toString(), MediaType.APPLICATION_JSON).build();
    } catch (Exception ex) {
      if (log.isErrorEnabled()) log.error(ex.getMessage());
    }
    return Response.ok("{\"result\": \"failed\"}", MediaType.TEXT_PLAIN).build();
  }

  @POST
  @Path("/editComment")
  @RolesAllowed("users")
  public Response editComment(MultivaluedMap<String, String> data) {
    String commentPath = data.getFirst("commentPath");
    String newComment = data.getFirst("newComment");
    String ws = data.getFirst("ws");
    CommentsService commentsService = WCMCoreUtils.getService(CommentsService.class);
    JSONObject obj = new JSONObject();
    try {
      Node nodeComment = getNode(commentPath, ws);
      commentsService.updateComment(nodeComment, newComment);

      obj.put("result", true);
      return Response.ok(obj.toString(), MediaType.APPLICATION_JSON).build();
    } catch (Exception ex) {
      if (log.isErrorEnabled()) log.error(ex.getMessage());
    }
    return Response.ok("{\"result\": \"failed\"}", MediaType.TEXT_PLAIN).build();
  }


  @POST
  @Path("/delComment")
  @RolesAllowed("users")
  public Response delComment(MultivaluedMap<String, String> data) {
    String commentPath = data.getFirst("commentPath");
    String ws = data.getFirst("ws");
    CommentsService commentsService = WCMCoreUtils.getService(CommentsService.class);
    JSONObject obj = new JSONObject();
    try {
      Node nodeComment = getNode(commentPath, ws);
      commentsService.deleteComment(nodeComment);
      obj.put("result", true);
      return Response.ok(obj.toString(), MediaType.APPLICATION_JSON).build();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Response.ok("{\"result\": \"failed\"}", MediaType.TEXT_PLAIN).build();
  }

  @POST
  @Path("/getComment")
  @RolesAllowed("users")
  public Response getComment(MultivaluedMap<String, String> data) {
      String commentPath = data.getFirst("commentPath");
    String ws = data.getFirst("ws");

    JSONObject obj = new JSONObject();
    try {
      Node nodeComment = getNode(commentPath, ws);

      if (nodeComment.hasProperty("exo:commentContent")) {
        obj.put("result", true);
        obj.put("commentContent", nodeComment.getProperty("exo:commentContent").getString());
        obj.put("commentPath", nodeComment.getPath());
        return Response.ok(obj.toString(), MediaType.APPLICATION_JSON).build();
      }
    } catch (Exception e) {
      if (log.isErrorEnabled()) log.error(e.getMessage());
    }
    return Response.ok("{\"result\": \"failed\"}", MediaType.TEXT_PLAIN).build();
  }

  private Node getNode(String nodePath, String ws) throws RepositoryException {
    SessionProvider sessionProvider = WCMCoreUtils.getUserSessionProvider();
    ManageableRepository manageableRepository = WCMCoreUtils.getRepository();
    Session session = sessionProvider.getSession(ws, manageableRepository);
    return (Node) session.getItem(nodePath);
  }

}
