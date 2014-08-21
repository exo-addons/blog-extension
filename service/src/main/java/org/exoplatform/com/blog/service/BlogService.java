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
import java.util.List;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 4, 2014
 *
 * Blog-extension service, provider methods for blog extension
 */
public interface BlogService {

  /**
   * Get all blog archive years
   * @return
   */
  public List<Integer> getArchiveYears();

  /**
   * Get all archive month by year
   * @param year
   * @return
   */
  public List<Integer> getArchiveMonths(int year);

  /**
   * Get total post of year
   * @param year
   * @return
   */
  public int getArchivesCountInYear(int year);

  /**
   * Get total post of  month
   * @param year
   * @param month
   * @return
   */
  public int getArchivesCountInMonth(int year, int month);

  /**
   * Get all blogs by year/month
   * @param year
   * @param month
   * @return
   */
  public List<Node> getPosts(int year, int month);

  /**
   * Increase post count from blog-archive cached table. when add new a post.
   * @param postNode
   */
  public void addPost(Node postNode);

  /**
   * Decrease post count from blog-archive cached table. when remove a post.
   * @param postNode
   */
  public void removePost(Node postNode);

  /**
   * Update status to approve|disapprove a post
   * @param nodePath
   */
  public boolean changeStatus(String postPath, String nodePath);

  /**
   * Vote a post of blog by user/language
   * @param postPath
   * @param score
   * @return
   */
  public boolean vote(String postPath, double score);

  /**
   * get a comment of post by comment path
   * @param nodePath
   * @return
   */
  public Node getCommentNode(String nodePath);

  /**
   * Edit a comment of post
   * @param nodeToEdit
   * @param newComment
   * @return
   */
  public boolean editComment(String nodeToEdit, String newComment);

  /**
   * Delete a comment of post
   *
   * @param nodePath
   * @return
   */
  public boolean delComment(String nodePath);

  /**
   * To increase a blog-post view count when user visited article
   * @param nodeToUpdate
   */
  public void increasePostView(Node nodeToUpdate);

  /**
   * Get total visited of blog-post
   * @param node
   * @return
   */
  public long getPostViewCount(Node node);

}
