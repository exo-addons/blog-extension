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

package org.exoplatform.com.blog.service.entity;

import java.util.Map;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 5, 2014
 *
 * Build blog-archive cache table structure
 */
public class BlogArchive{
  private int year_post;
  private Map<Integer, Integer> month;

  public int getYear_post() {
    return year_post;
  }

  public void setYear_post(int year_post) {
    this.year_post = year_post;
  }

  public Map<Integer, Integer> getMonth() {
    return month;
  }

  public void setMonth(Map<Integer, Integer> month) {
    this.month = month;
  }
}
