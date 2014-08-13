(function (gj, sharethis) {
  function blog() {
  };

  blog.prototype.test = function () {
    alert('Blog\'s js worked! ');
  };

  blog.prototype.getPost = function (el, year, month) {
    gj.ajax({
      url: "/portal/rest/blog/service/get-blogs?year=" + year + "&month=" + month,
      dataType: "text",
      type: "POST"
    })
        .success(function (data) {
          var _blogs = gj.parseJSON(data);
          var html = "";
          gj.each(_blogs, function (key, val) {
            var link = "/portal/intranet/blog/article?content-id=/repository/collaboration" + val.postPath;
            html += "<div> <a href='" + link + "' >" + val.postTitle + "</a></div>";
          });
          gj(".blog-archive-post-link").html('');
          gj("#month-" + year + "-" + month).html(html);
        })
  }

  blog.prototype.blogArchiveAccordionInit = function () {
    gj(".blog_archive_accordion").blogArchiveAccordion();
  }

  gj.fn.blogArchiveAccordion = function (options) {
    if (this.length > 1) {
      this.each(function () {
        gj(this).blogArchiveAccordion(options);
      });
      return this;
    }
    var settings = gj.extend({
      animation: true,
      showIcon: true,
      closeAble: false,
      closeOther: true,
      slideSpeed: 150,
      activeIndex: false
    }, options);
    var plugin = this;
    var init = function () {
      plugin.createStructure();
      plugin.clickHead();
    }
    this.createStructure = function () {
      plugin.addClass('smk_accordion');
      if (settings.showIcon) {
        plugin.addClass('acc_with_icon');
      }
      if (plugin.find('.accordion_in').length < 1) {
        plugin.children().addClass('accordion_in');
      }
      plugin.find('.accordion_in').each(function (index, elem) {
        var childs = gj(elem).children();
        gj(childs[0]).addClass('acc_head');
        gj(childs[1]).addClass('acc_content');
      });
      //Append icon
      if (settings.showIcon) {
        plugin.find('.acc_head').prepend('<div class="acc_icon_expand"></div>');
      }
      //Hide inactive
      plugin.find('.accordion_in .acc_content').not('.acc_active .acc_content').hide();
      //Active index
      if (settings.activeIndex === parseInt(settings.activeIndex)) {
        if (settings.activeIndex === 0) {
          plugin.find('.accordion_in').addClass('acc_active').show();
          plugin.find('.accordion_in .acc_content').addClass('acc_active').show();
        }
        else {
          plugin.find('.accordion_in').eq(settings.activeIndex - 1).addClass('acc_active').show();
          plugin.find('.accordion_in .acc_content').eq(settings.activeIndex - 1).addClass('acc_active').show();
        }
      }
    }
    // Action when the user click accordion head
    this.clickHead = function () {
      plugin.on('click', '.acc_head', function () {
        var s_parent = gj(this).parent();
        if (s_parent.hasClass('acc_active') == false) {
          if (settings.closeOther) {
            plugin.find('.acc_content').slideUp(settings.slideSpeed);
            plugin.find('.accordion_in').removeClass('acc_active');
          }
        }
        if (s_parent.hasClass('acc_active')) {
          if (false !== settings.closeAble) {
            s_parent.children('.acc_content').slideUp(settings.slideSpeed);
            s_parent.removeClass('acc_active');
          }
        }
        else {
          gj(this).next('.acc_content').slideDown(settings.slideSpeed);
          s_parent.addClass('acc_active');
        }
      });
    }
    init();
    return this;
  };

  //postform
  blog.prototype.syncuri = function (dateTime) {
    var name = gj("#name");
    var data = name.val();
    data = dateTime + "/" + data;
    gj('#uri').replaceWith('<span id="uri">' + data + '</span>');
  }

  gj("#title").change(function () {
    var name = gj("#name");
    if (!name.readOnly) {
      var title = this.value;
      var portalContext = eXo.env.portal.context;
      var portalRest = eXo.env.portal.rest;
      var url = portalContext + "/" + portalRest + "/l11n/cleanName";
      gj.ajax({
        type: "GET",
        url: url,
        data: { name: title},
        success: function (data) {
          gj('#name').val(data).trigger('change');
        }
      }); // end ajax
    } // end if not readonly
  }); // end change title
  gj("#name").change(blog.prototype.syncuri);

  blog.prototype.postComment = function (uuid) {
    var aform = gj("#commentform-" + uuid);
    var comment = aform.find('input[name="comment"]').val();
    var path = aform.find('input[name="jcrPath"]').val();
    gj.ajax({
      type: "POST",
      url: "/rest/contents/comment/add",
      data: { comment: comment, jcrPath: path},
      success: function () {
        gj('#respond-$uuid').html("<div id='message'></div>");
        gj('#message').html('<div class="alert alert-success"><p>Your comment has been posted.</p></div>')
            .hide()
            .fadeIn(1500);
      }
    }); // end ajax

    return false;
  }; // end click on button

  //sharethis
  blog.prototype.intShare = function () {
    var switchTo5x = true;
    sharethis.stLight.options({publisher: "131ce3d5-a240-42f0-9945-f882036f2d00", doNotHash: false, doNotCopy: false, hashAddressBar: false});
  };
  // approve a post
  blog.prototype.changeStatus = function (elId, nodePath, postPath) {
    if (confirm("Are u sure?")) {
      var obj = new Object();
      obj.nodePath = nodePath;
      obj.postPath = postPath;
      gj.ajax({
        url: "/portal/rest/blog/service/changeStatus",
        dataType: "text",
        data: obj,
        type: "POST"
      })
          .success(function (data) {
            var rs = gj.parseJSON(data);
            console.log(data);

            var btn = '<input type="button" class="btn" onclick="eXo.ecm.blog.changeStatus(\'' + elId + '\', \'' + nodePath + '\');"';
            if (!rs.result) {
              btn += ' value="Approve"';
              gj('#' + elId).removeClass('approved');
              gj('#' + elId).addClass('disapproved');
            } else {
              btn += ' value="Disapprove"';
              gj('#' + elId).removeClass('disapproved');
              gj('#' + elId).addClass('approved');
            }
            btn += ' />';

            gj('#approve-' + elId).html(btn);
          })
    }
  }

  //rate
  gj.rate_wrapper = {
    el: null,
    init: function (options, el) {
      this.settings = gj.extend({}, {
            on_select: function (ui, score) {
            },
            uid: 0,
            star_count: 5,
            start_z: 100,
            cookie_name: 'mysite_product_score',
            cookie_domain: '',
            cookie_path: '/',
            cookie_duration: 365
          }, options
      );
      //set the current element
      this.el = el;
      var uid = this.settings.uid;

      //use the score in the cookies when we have them;
      this.set_score(el, uid);

      stars = el.find('a');
      stars.each(function (i, star) {
        gj(star).bind('click', function (ev) {
          score = +gj(star).html();
          ev.preventDefault();
          gj.rate_wrapper.settings.on_select(el, score);
          gj.rate_wrapper.set_cookie_score(uid, score);
          gj.rate_wrapper.set_score(el, uid, score);
        });
      });

      // Hide the current score when we have a mousever
      // and show em again on leave.
      stars.each(function (i, star) {
        gj(star).hover(function () {
              el.find('.rate-current-score').hide();
            },
            function () {
              el.find('.rate-current-score').show();
            }
        );
      });
    },
    set_score: function (el, uid, score) {

      cur_score = el.find('.rate-current-score');
      cur_score_val = cur_score.html();
      cookie_score_val = this.get_rating(uid);

      if (cookie_score_val !== 0) {
        cur_score_val = cookie_score_val;
      }

      if (score > 0) {
        cur_score_val = score / gj.rate_wrapper.settings.star_count * 100;
      }
      cur_score.css('display', 'block').css('width', String(cur_score_val) + '%');
    },
    /** retrieve the cookie value **/
    get_cookie: function (c_name) {
      if (document.cookie.length > 0) {
        c_start = document.cookie.indexOf(c_name + "=");
        if (c_start != -1) {
          c_start = c_start + c_name.length + 1;
          c_end = document.cookie.indexOf(";", c_start);
          if (c_end == -1) c_end = document.cookie.length;
          return unescape(document.cookie.substring(c_start, c_end));
        }
      }
      return "";
    },
    /** Retrives the current rating inside the cookie **/
    get_rating: function (uid) {
      uid = String(uid);

      cookie = this.get_cookie(gj.rate_wrapper.settings.cookie_name);

      if ('' === cookie) {
        return 0;
      }

      cookie = eval("(" + cookie + ")")
      if (uid in cookie) {

        return cookie[uid] / gj.rate_wrapper.settings.star_count * 100;
      }
      return 0;
    },
    set_cookie_score: function (uid, score) {
      var duration = new Date();
      duration.setDate(duration.getDate() + gj.rate_wrapper.settings.cookie_duration);

      cookie_name = gj.rate_wrapper.settings.cookie_name;
      cookie_domain = gj.rate_wrapper.settings.cookie_domain;
      cookie_path = gj.rate_wrapper.settings.cookie_path;
      cookie_duration = duration.toUTCString()

      cookie_val = this.get_cookie(cookie_name);
      o = new Object;
      o[String(uid)] = score;

      if ('' === cookie_val) {
        cookie_val = o;
      } else {
        cookie_val = eval("(" + cookie_val + ")");
        cookie_val = gj.extend({}, cookie_val, o);
      }

      cookie_val = escape(JSON.stringify(cookie_val));
      cookie = cookie_name + '=' + cookie_val + ';expires=' + cookie_duration + ';path=' + cookie_path;
      if ('' !== cookie_domain) {
        cookie += ';domain=' + cookie_domain
      }
      document.cookie = cookie;
    }
  }
  gj.fn.rate = function (options) {
    return gj.rate_wrapper.init(options, gj(this));
  }

  gj('.rate-wrapper').each(function(i, item) {
    gj(item).rate({
      uid: gj(item).find('input').val(),
      on_select: function(ui, score) {
        console.log('ui: '+ui);
        console.log('score: '+score);
        //send out our ajax call
        var p = ui.find('input');
        //alert('sending out product_id: ' + p.val() + ' with score ' + score);
      },
      cookie_domain: '.mydomain.com',
      cookie_name: 'my-rating-cookie-name'
    });
  });




  eXo.ecm.blog = new blog();
  return eXo.ecm.blog;
  //-------------------------------------------------------------------------//
})(gj, sharethis);