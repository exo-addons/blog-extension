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

package org.exoplatform.com.blog.service;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.util.List;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 * exo@exoplatform.com
 * Aug 4, 2014
 * <p/>
 * Blog-extension service, provider methods for blog extension
 */
public interface BlogService {

  /**
   * Get all blog archive years
   *
   * @return
   */
  public List<Integer> getArchiveYears();

  /**
   * Get all archive month by year
   *
   * @param year
   * @return
   */
  public List<Integer> getArchiveMonths(int year);

  /**
   * Get total post of year
   *
   * @param year
   * @return
   */
  public int getArchivesCountInYear(int year);

  /**
   * Get total post of  month
   *
   * @param year
   * @param month
   * @return
   */
  public int getArchivesCountInMonth(int year, int month);

  /**
   * Get all blogs by year/month
   *
   * @param year
   * @param month
   * @return
   */
  public List<Node> getPosts(int year, int month);

  /**
   * Increase post count from blog-archive cached table. when add new a post.
   *
   * @param postNode
   */
  public void addPost(Node postNode);

  /**
   * Decrease post count from blog-archive cached table. when remove a post.
   *
   * @param postNode
   */
  public void removePost(Node postNode);

  /**
   * Update status to approve|disapprove a post's comment
   *
   * @param postNode
   * @param commentNode
   */
  public Node changeCommentStatus(Node postNode, Node commentNode);

  /**
   * To increase a blog-post view count when user visited article
   *
   * @param nodeToUpdate
   */
  public void increasePostView(Node nodeToUpdate);

  /**
   * Get total visited of post
   *
   * @param postNode
   * @return
   */
  public long getPostViewCount(Node postNode);


  /**
   * Get count of post comments
   * Using in blog-article detail viewpost.gtmpl
   * @param postNode
   * @return
   */
  public long getPostComments(Node postNode);

  /**
   * Get last comment added
   * While end user add new comment on a post. This function return that node.
   * @param postNode
   * @return
   */
  public Node getLastComment(Node postNode);

  /**
   * Get post comments
   * Using for lazy load comment
   * @param limit
   * @param offset
   * @return
   */
  public NodeIterator getComments(Node postNode, long limit, long offset);

}
