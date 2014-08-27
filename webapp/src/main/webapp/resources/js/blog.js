(function (gj, sharethis) {
  function blog() {
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
      showIcon: false,
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
        plugin.find('.acc_head').prepend('<i class="uiIconArrowRight uiIconLightGray"></i>');
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

        if (s_parent.hasClass('acc_active') == false) {  //not active, remove actice class
          if (settings.closeOther) {
            plugin.find('.acc_content').slideUp(settings.slideSpeed);
            plugin.find('.accordion_in').removeClass('acc_active');
            plugin.find('.uiIconLightGray').removeClass('uiIconArrowDown');
            plugin.find('.uiIconLightGray').addClass('uiIconArrowRight');
          }
        }

        if (s_parent.hasClass('acc_active')) { //actived
          if (false !== settings.closeAble) {
            s_parent.children('.acc_content').slideUp(settings.slideSpeed);
            s_parent.removeClass('acc_active');
            s_parent.children('.uiIconLightGray').removeClass('uiIconArrowRight');
            gj(this).children('.uiIconLightGray').addClass('uiIconArrowDown');
          }else{
            s_parent.children('.acc_content').slideUp(settings.slideSpeed);
            s_parent.removeClass('acc_active');
            s_parent.children('.uiIconLightGray').removeClass('uiIconArrowDown');
            gj(this).children('.uiIconLightGray').addClass('uiIconArrowRight');
          }
        }
        else {
          gj(this).next('.acc_content').slideDown(settings.slideSpeed);
          s_parent.addClass('acc_active');
          gj(this).children('.uiIconLightGray').removeClass('uiIconArrowRight');
          gj(this).children('.uiIconLightGray').addClass('uiIconArrowDown');
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

  function getBlogTime(date){
    var month = new Array(); month[0] = "Jan"; month[1] = "Feb"; month[2] = "Mar";
    month[3] = "Apr"; month[4] = "May"; month[5] = "Jun"; month[6] = "Jul";
    month[7] = "Aug"; month[8] = "Sep"; month[9] = "Oct";
    month[10] = "Nov"; month[11] = "Dec";
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12;
    hours = hours ? hours : 12;
    minutes = minutes < 10 ? '0'+minutes : minutes;
    var strTime = hours + ':' + minutes + ' ' + ampm;
    var result =	"on "+month[date.getMonth()] +" "+ date.getDate() + ", " + date.getFullYear()+" at "+strTime;
    return result;
  }
  blog.prototype.prePostComment = function (e, uuid) {
    if(e.keyCode === 13){
      blog.prototype.postComment(uuid);
      return false;
    }//else{console.log('init....')}
  };

  blog.prototype.postComment = function (uuid) {
    var aform = gj("#commentform-" + uuid);
    var comment = aform.find('input[name="comment"]').val();
    var type = aform.find('input[name="type"]').val();
    if(comment===''){
      //alert('Comment message count empty!');
      aform.find('input[name="comment"]').focus();
      return false;
    }

    var action = aform.find('input[name="action"]').val();

    if(action === "Edit"){
      eXo.ecm.blog.editComment(uuid, '');
      return false;
    }
    var path = aform.find('input[name="jcrPath"]').val();
    gj.ajax({
      type: "POST",
      url: "/rest/contents/comment/add",
      data: { comment: comment, jcrPath: path},
      success: function () {
        gj.ajax({
          type: "POST",
          url: "/portal/rest/blog/service/getLastComment",
          data: {jcrPath: path},
          async:false,
          success: function (data) {
            if(data.result){
              var commentContent = data.commentContent;
              var commentPath = data.commentPath;
              var ws = data.ws;
              var totalComment = data.totalComment;
              var postPath = "";
              var _path = path.split("/");
              var repo = _path[1];
              var _ws = _path[2];
              postPath = path.substr(repo.length+_ws.length+2, path.length);
              if (postPath.charAt(1)==='/') postPath.substr(1, postPath.length);

              var viewer = aform.find('input[name="viewer"]').val();
              var fme = aform.find('input[name="fme"]').val();
              var avatar = aform.find('input[name="avatar"]').val();
              var date = new Date();
              var timeId = date.getTime();

              var dateStr = getBlogTime(date);

              //var result = new StringBuffer();
              var result="<ul class=\"commentList children\">";
              result += "<li class=\"commentItem\" id=\"comment-"+timeId+"\">";
              result += "	<div class=\"commentLeft\">";
              result += "		<a class=\"avatarXSmall\" href=\""+avatar+"\" rel=\"tooltip\" data-placement=\"bottom\" data-original-title=\""+fme+"\">";
              result += "			<img alt=\""+fme+"\" src=\""+avatar+"\">";
              result += "		</a>";
              result += "	</div>"; // end comment left

              result += "<div class=\"commentRight\">";
              result += "<div class=\"author\">";
              result += "	<a href=\"/portal/intranet/profile/"+viewer+"\">"+fme+"</a>";
              result += "	<span class=\"dateTime\">"+dateStr+"</span> &nbsp; &nbsp;";
              result += "<input name=\"cmtPath\" type=\"hidden\" value=\""+commentPath+"\" />";
              result += "<input name=\"ws\" type=\"hidden\" value=\""+ws+"\" />";
              result += "<input name=\"avatar\" type=\"hidden\" value=\""+avatar+"\" />";
              result += "<input name=\"viewer\" type=\"hidden\" value=\""+viewer+"\" />";
              result += "<input name=\"fme\" type=\"hidden\" value=\""+fme+"\" />";
              result += "<input name=\"postPath\" type=\"hidden\" value=\""+postPath+"\" />";

              result += "	<span class=\"reply actionIcon\" onclick=\"eXo.ecm.blog.replyComment("+timeId+")\" ><i class=\"uiIconReply uiIconLightGray\"></i> Reply</span>";
              result += " <span id=\"approve-"+timeId+"\" class=\"pull-right approve\">";
              result += "	<input type=\"button\" class=\"btn\" onclick=\"eXo.ecm.blog.changeStatus("+timeId+", '"+commentPath+"', '/Grrs/D', '"+ws+"');\" value=\"Disapprove\">";
              result += " </span>"

              result +=	"	<div class=\"contentComment\">";
              result += "	  <span class=\"ContentBlock\"  id=\""+timeId+"\">"+commentContent;
              result += "	  </span>";
              result += "	  <span>";
              result += " 		<a class=\"actionIcon\" href=\"javascript:void(0);\" onclick=\"eXo.ecm.blog.loadToEdit('"+commentPath+"', '"+uuid+"', '"+timeId+"', '"+ws+"')\"><i class=\"uiIconLightGray uiIconEdit\"></i></a>";
              result += "			<a class=\"actionIcon\" href=\"javascript:void(0);\" onclick=\"eXo.ecm.blog.deleteComment('"+commentPath+"', '"+timeId+"' ,'"+ws+"')\"><i class=\"uiIconLightGray uiIconDelete\"></i></a>";
              result += "		</span>";
              result += "	</div>"

              result += "</div>";
              //  result += "</div>";
              result += "</li></ul>";

              if(type !== 'rootPostComment'){
                gj("#comment-form-"+uuid+" .commentItem").has("form").remove();
                gj("#comment-"+uuid).append(result);
              }else{
                gj("#commentList").append(result);
              }
              gj("#commentInputBox input[name=comment]").val('');
              var plural="";
              if(totalComment>1){plural="s";}
              gj("#total-comment").html(totalComment + " comment" + plural);
            }else{//end if (_data.result=false)
              alert("Error, u can trial again!.");
            }
          }

        }); //end ajax get last comment
      } // end sucess 
    }); // end ajax

    return false;
  }; // end click on button

  //sharethis
  blog.prototype.intShare = function () {
    var switchTo5x= true;
    sharethis.stLight.options({publisher: "131ce3d5-a240-42f0-9945-f882036f2d00", doNotHash: false, doNotCopy: false, hashAddressBar: false});
  };
  // approve a post
  blog.prototype.changeStatus = function (elId, nodePath, postPath, ws) {
    if (confirm("Are u sure?")) {
      var obj = new Object();
      obj.nodePath = nodePath;
      obj.postPath = postPath;
      obj.ws = ws;
      gj.ajax({
        url: "/portal/rest/blog/service/changeCommentStatus",
        dataType: "text",
        data: obj,
        type: "POST"
      })
          .success(function (data) {
            var rs = gj.parseJSON(data);
            var btn = '<input type="button" class="btn" onclick="eXo.ecm.blog.changeStatus(\'' + elId + '\', \'' + nodePath + '\',  \'' + postPath + '\',\''+ws+'\');"';
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

        //send out our ajax call
        var obj = new Object();
        obj.score = score;
        obj.postPath = ui.find("input[name=postPath]").val();
        obj.ws = ui.find("input[name=ws]").val()
        gj.ajax({
          url: "/portal/rest/blog/service/updateVote",
          dataType: "text",
          data: obj,
          type: "POST"
        })
            .success(function (data) {

            })
        var p = ui.find('input');
        //alert('sending out product_id: ' + p.val() + ' with score ' + score);
      },
      cookie_domain: '.mydomain.com',
      cookie_name: 'my-rating-cookie-name'
    });
  });

  //scroll
  var ScrollToTop = function (options) {
    this.gjdoc = gj('body');
    this.options = gj.extend(ScrollToTop.defaults, options);

    var namespace = this.options.namespace;

    if (this.options.skin === null) {
      this.options.skin = 'cycle';
    }

    this.classes = {
      skin: namespace + '_' + this.options.skin,
      trigger: namespace,
      animating: namespace + '_animating',
      show: namespace + '_show'
    };

    this.disabled = false;
    this.useMobile = false;
    this.isShow = false;

    var self = this;
    gj.extend(self, {
      init: function () {
        self.transition = self.transition();
        self.build();


        if (self.options.target) {
          if (typeof self.options.target === 'number') {
            self.target = self.options.target;
          } else if (typeof self.options.target === 'string') {
            self.target = Math.floor(gj(self.options.target).offset().top);
          }
        } else {
          self.target = 0;
        }

        self.gjtrigger.on('click.scrollToTop', function () {
          self.gjdoc.trigger('ScrollToTop::jump');
          return false;
        });

        // bind events
        self.gjdoc.on('ScrollToTop::jump', function () {
          if (self.disabled) {
            return;
          }

          self.checkMobile();

          var speed, easing;

          if (self.useMobile) {
            speed = self.options.mobile.speed;
            easing = self.options.mobile.easing;
          } else {
            speed = self.options.speed;
            easing = self.options.easing;
          }

          self.gjdoc.addClass(self.classes.animating);


          if (self.transition.supported) {
            var pos = gj(window).scrollTop();

            self.gjdoc.css({
              'margin-top': -pos + self.target + 'px'
            });
            gj(window).scrollTop(self.target);

            self.insertRule('.duration_' + speed + '{' + self.transition.prefix + 'transition-duration: ' + speed + 'ms;}');

            self.gjdoc.addClass('easing_' + easing + ' duration_' + speed).css({
              'margin-top': ''
            }).one(self.transition.end, function () {
              self.gjdoc.removeClass(self.classes.animating + ' easing_' + easing + ' duration_' + speed);
            });
          } else {
            gj('html, body').stop(true, false).animate({
              scrollTop: self.target
            }, speed, function () {
              self.gjdoc.removeClass(self.classes.animating);
            });
            return;
          }
        })
            .on('ScrollToTop::show', function () {
              if (self.isShow) {
                return;
              }
              self.isShow = true;

              self.gjtrigger.addClass(self.classes.show);
            })
            .on('ScrollToTop::hide', function () {
              if (!self.isShow) {
                return;
              }
              self.isShow = false;
              self.gjtrigger.removeClass(self.classes.show);
            })
            .on('ScrollToTop::disable', function () {
              self.disabled = true;
              self.gjdoc.trigger('ScrollToTop::hide');
            })
            .on('ScrollToTop::enable', function () {
              self.disabled = false;
              self.toggle();
            });

        gj(window).on('scroll', self._throttle(function () {
          if (self.disabled) {
            return;
          }

          self.toggle();
        }, self.options.throttle));

        if (self.options.mobile) {
          gj(window).on('resize', self._throttle(function () {
            if (self.disabled) {
              return;
            }

            self.checkMobile();
          }, self.options.throttle));
        }

        self.toggle();
      },
      checkMobile: function () {
        var width = gj(window).width();

        if (width < self.options.mobile.width) {
          self.useMobile = true;
        } else {
          self.useMobile = false;
        }
      },
      build: function () {
        if (self.options.trigger) {
          self.gjtrigger = gj(self.options.trigger);
        } else {
          self.gjtrigger = gj('<a href="#" class="' + self.classes.trigger + ' ' + self.classes.skin + '">' + self.options.text + '</a>').appendTo(gj('body'));
        }

        self.insertRule('.' + self.classes.show + '{' + self.transition.prefix + 'animation-duration: ' + self.options.animationSpeed + 'ms;' + self.transition.prefix + 'animation-name: ' + self.options.namespace + '_' + self.options.animation + ';}');

        if (self.options.mobile) {
          self.insertRule('@media (max-width: ' + self.options.mobile.width + 'px){.' + self.classes.show + '{' + self.transition.prefix + 'animation-duration: ' + self.options.mobile.animationSpeed + 'ms !important;' + self.transition.prefix + 'animation-name: ' + self.options.namespace + '_' + self.options.mobile.animation + '  !important;}}');
        }
      },
      can: function () {
        var distance;
        if (self.useMobile) {
          distance = self.options.mobile.distance;
        } else {
          distance = self.options.distance;
        }
        if (gj(window).scrollTop() > distance) {
          return true;
        } else {
          return false;
        }
      },
      toggle: function () {
        if (self.can()) {
          self.gjdoc.trigger('ScrollToTop::show');
        } else {
          self.gjdoc.trigger('ScrollToTop::hide');
        }
      },

      transition: function () {
        var e,
            end,
            prefix = '',
            supported = false,
            el = document.createElement("fakeelement"),
            transitions = {
              "WebkitTransition": "webkitTransitionEnd",
              "MozTransition": "transitionend",
              "OTransition": "oTransitionend",
              "transition": "transitionend"
            };
        for (e in transitions) {
          if (el.style[e] !== undefined) {
            end = transitions[e];
            supported = true;
            break;
          }
        }
        if (/(WebKit)/i.test(window.navigator.userAgent)) {
          prefix = '-webkit-';
        }
        return {
          prefix: prefix,
          end: end,
          supported: supported
        };
      },
      insertRule: function (rule) {
        if (self.rules && self.rules[rule]) {
          return;
        } else if (self.rules === undefined) {
          self.rules = {};
        } else {
          self.rules[rule] = true;
        }

        if (document.styleSheets && document.styleSheets.length) {
          document.styleSheets[0].insertRule(rule, 0);
        } else {
          var style = document.createElement('style');
          style.innerHTML = rule;
          document.head.appendChild(style);
        }
      },
      /**
       * _throttle
       * @description Borrowed from Underscore.js
       */
      _throttle: function (func, wait) {
        var _now = Date.now || function () {
          return new Date().getTime();
        };
        var context, args, result;
        var timeout = null;
        var previous = 0;
        var later = function () {
          previous = _now();
          timeout = null;
          result = func.apply(context, args);
          context = args = null;
        };
        return function () {
          var now = _now();
          var remaining = wait - (now - previous);
          context = this;
          args = arguments;
          if (remaining <= 0) {
            clearTimeout(timeout);
            timeout = null;
            previous = now;
            result = func.apply(context, args);
            context = args = null;
          } else if (!timeout) {
            timeout = setTimeout(later, remaining);
          }
          return result;
        };
      }
    });

    this.init();
  };

  // Default options
  ScrollToTop.defaults = {
    distance: 200,
    speed: 1000,
    easing: 'linear',
    animation: 'fade', // fade, slide, none
    animationSpeed: 500,

    mobile: {
      width: 768,
      distance: 100,
      speed: 1000,
      easing: 'easeInOutElastic',
      animation: 'slide',
      animationSpeed: 200
    },

    trigger: null, // Set a custom triggering element. Can be an HTML string or jQuery object
    target: null, // Set a custom target element for scrolling to. Can be element or number
    text: 'Scroll To Top', // Text for element, can contain HTML

    skin: null,
    throttle: 250,

    namespace: 'scrollToTop'
  };

  ScrollToTop.prototype = {
    constructor: ScrollToTop,
    jump: function () {
      this.gjdoc.trigger('ScrollToTop::jump');
    },
    disable: function () {
      this.gjdoc.trigger('ScrollToTop::disable');
    },
    enable: function () {
      this.gjdoc.trigger('ScrollToTop::enable');
    },
    destroy: function () {
      this.gjtrigger.remove();
      this.gjdoc.data('ScrollToTop', null);
      this.gjdoc.off('ScrollToTop::enable')
          .off('ScrollToTop::disable')
          .off('ScrollToTop::jump')
          .off('ScrollToTop::show')
          .off('ScrollToTop::hide');
    }
  };

  gj.fn.scrollToTop = function (options) {
    if (typeof options === 'string') {
      var method = options;
      var method_arguments = arguments.length > 1 ? Array.prototype.slice.call(arguments, 1) : undefined;

      return this.each(function () {
        var api = gj.data(this, 'scrollToTop');

        if (api && typeof api[method] === 'function') {
          api[method].apply(api, method_arguments);
        }
      });
    } else {
      return this.each(function () {
        var api = gj.data(this, 'scrollToTop');
        if (!api) {
          api = new ScrollToTop(options);
          gj.data(this, 'scrollToTop', api);
        }
      });
    }
  };

  blog.prototype.initScroll = function(){
    gj('body').scrollToTop();
    gj( ".scrollToTop" ).attr("title", "Back to top");
  }

  //edit comment
  blog.prototype.loadToEdit = function(commentPath, postUUID, timeId, ws){
    var obj = new Object();
    obj.commentPath = commentPath;
    obj.ws = ws;
    gj.ajax({
      url: "/portal/rest/blog/service/getComment",
      dataType: "text",
      data:obj,
      type: "POST"
    })
        .success(function (data) {
          var _result = gj.parseJSON(data);
          if(_result.result){
            var type = gj('#commentform-'+postUUID+' input[name=type]').val();
            if(type !== 'rootPostComment'){
              blog.prototype.replyComment(postUUID);
            }else{
              gj('#commentform-'+postUUID+' input[name=commentCancel]').removeAttr("style");
            }
            gj('#commentform-'+postUUID+' input[name=comment]').val(_result.commentContent);
            gj('#commentform-'+postUUID+' input[name=comment]').focus();
            gj('#commentform-'+postUUID+' input[name=commentPath]').val(_result.commentPath);
            gj('#commentform-'+postUUID+' input[name=timeId]').val(timeId);
            gj('#commentform-'+postUUID+' input[name=ws]').val(ws);
            gj('#commentform-'+postUUID+' input[name=action]').val("Edit");
            gj('#commentform-'+postUUID+' input[name=submit]').attr("value","Update Comment");
            gj('#commentform-'+postUUID+' input[name=submit]').attr("onclick","eXo.ecm.blog.editComment('"+postUUID+"')");
          }else{
            alert('Comment in '+commentPath+ ' doesnt exist!');
          }
        })

  }

  blog.prototype.deleteComment = function(commentPath, commendId, ws){
    if (confirm("Are u sure?")) {
      var obj = new Object();
      if (commentPath.charAt(1)==='/') commentPath.substr(1, commentPath.length);

      obj.commentPath = commentPath;
      obj.ws = ws;
      gj.ajax({
        type: "POST",
        data:obj,
        url: "/portal/rest/blog/service/delComment",
        success: function (data) {
          if(data.result){
            var totalComment = data.totalComment;
            var plural="";
            if(totalComment>1){plural="s";}
            gj('#comment-'+commendId).remove();
            gj('#total-comment').html(totalComment + " comment" +plural);
          }else{
            alert('Delete comment failed. Please retry again!.');
          }
        }
      }); // end ajax
    }
  }
  /**
   Edit a comment
   */
  blog.prototype.editComment = function(uuid){
    var aform = gj("#commentform-" + uuid);
    var comment = aform.find('input[name="comment"]').val();
    var path = aform.find('input[name="commentPath"]').val();
    var timeId = aform.find('input[name="timeId"]').val();
    var ws = aform.find('input[name="ws"]').val();
    var type = aform.find('input[name="type"]').val();
    var obj = new Object();
    obj.commentPath = path;
    obj.newComment = comment;
    obj.ws=ws;
    gj.ajax({
      type: "POST",
      url: "/portal/rest/blog/service/editComment",
      data: obj,
      success: function (data) {
        if(data.result){
          gj('#'+timeId).html(comment);
          var destination = gj('#comment-'+uuid+" span .ContentBlock");

          destination.html(comment);

          if(type !== 'rootPostComment'){
            gj('#commentform-'+uuid).closest("ul").remove();
          }
          gj('#commentform-'+uuid+' input[name=submit]').attr("value","Post Comment");
          gj('#commentform-'+uuid+' input[name=submit]').attr("id","btn-"+uuid);
          gj('#commentform-'+uuid+' input[name=submit]').attr("onclick","eXo.ecm.blog.postComment('"+uuid+"')");
          gj('#commentform-'+uuid+' input[name=commentCancel]').attr("style","display:none;");
          gj('#commentform-'+uuid+' input[name=action').val('');
          gj('#commentform-'+uuid+' input[name=comment').val('');
          /*
           gj('html, body').animate({
           scrollTop: destination.offset().top
           }, 2000);
           */
        }else{
          alert('Edit comment failed. Please retry again!.');
          aform.find('input[name="comment"]').focus();

          return false;
        }
      }
    }); // end ajax
  }

  blog.prototype.commentCancel = function(commentId, type){
    if(type==='rootPostComment'){
      gj('#commentform-'+commentId+' input[name=commentCancel]').attr("style", "display:none;");
      gj('#commentform-'+commentId+' input[name=submit]').attr("value", "Post Comment");
      gj('#commentform-'+commentId+' input[name=comment]').val('');
    }
    gj("#comment-"+commentId+" ul:last-child ").has("form").remove();
  }
  /**
   reply a comment
   */
  blog.prototype.replyComment = function(commentId){
    var commentItem = gj("#comment-"+commentId);
    var commentPath = commentItem.find('input[name="cmtPath"]').val();
//		var postPath = commentItem.find('input[name="postPath"]').val();
    var ws = commentItem.find('input[name="ws"]').val();
    var avatar = commentItem.find('input[name="avatar"]').val();
    var viewer = commentItem.find('input[name="viewer"]').val();

    gj("#comment-"+commentId+" ul:last-child ").has("form").remove();

    var commentForm="";
    commentForm += "<ul class=\"commentList children\" id=\"comment-form-"+commentId+"\" >";
    commentForm += "<li class=\"commentItem\">";

    commentForm += "<div class=\"commentItem commentFormBox clearfix\">";
    commentForm += "	<div class=\"commentLeft\">";
    commentForm += "		<a data-original-title=\""+viewer+"\" href=\"/portal/intranet/profile/"+viewer+"\" data-placement=\"bottom\" rel=\"tooltip\" class=\"avatarXSmall\">";
    commentForm += "			<img alt=\""+viewer+"\" src=\""+avatar+"\">";
    commentForm += "		</a>";
    commentForm += "	</div><!--end commentLeft-->";
    commentForm += "	<div class=\"commentRight\">";
    commentForm += "		<div id=\"commentInputBox\" class=\"commentInputBox\">";
    commentForm += "			<form name=\"commentform-"+commentId+"\" id=\"commentform-"+commentId+"\" class=\"form-inline media\">";
    commentForm += "				<input type=\"button\" onclick=\"eXo.ecm.blog.commentCancel('"+commentId+"')\" data-original-title=\"Cancel\" id=\"btn-cancel"+commentId+"\" data-placement=\"bottom\" rel=\"tooltip\" class=\"btn pull-right\" value=\"Cancel\" />";

    commentForm += "				<input type=\"button\" name=\"submit\" onclick=\"eXo.ecm.blog.postComment('"+commentId+"')\" data-original-title=\"Reply Comment\" id=\"btn-"+commentId+"\" data-placement=\"bottom\" rel=\"tooltip\" class=\"btn btn-primary  pull-right\" value=\"Reply\" />";
    commentForm += "					<div class=\"media-body\">";
    commentForm += "					<input name=\"avatar\" type=\"hidden\" value=\""+avatar+"\" />";
    commentForm += "					<input name=\"viewer\" type=\"hidden\" value=\""+viewer+"\" />";
    commentForm += "					<input name=\"fme\" type=\"hidden\" value=\""+viewer+"\" />";
    commentForm += "					<input name=\"timeId\" type=\"hidden\" value=\""+commentId+"\" />";
    commentForm += "					<input name=\"ws\" type=\"hidden\" value=\""+ws+"\" />";
    commentForm += "					<input name=\"action\" type=\"hidden\" value=\"\" />";
    commentForm += "					<input name=\"commentPath\" type=\"hidden\" value=\"/repository/collaboration"+commentPath+"\"/>";
    commentForm += "					<input name=\"jcrPath\" type=\"hidden\" value=\"/repository/collaboration"+commentPath+"\"/>";
    commentForm += "					<input type=\"text\" onkeydown=\"return eXo.ecm.blog.prePostComment(event, '"+commentId+"')\" style=\"width:100%\" tabindex=\"0\" placeholder=\"Your Reply Comment Here\" id=\"comment-"+commentId+"\" name=\"comment\">";
    commentForm += "					</div>";
    commentForm += "			</form>";
    commentForm += "		</div> <!--end comment input box-->";
    commentForm += "	</div> <!--end comment right-->";
    commentForm += "</div>";
    commentForm += "</li></ul>";

    commentItem.append(commentForm);
    gj("#commentform-"+commentId).find('input[name="comment"]').focus();
  }
  //context-menu in comment

  eXo.ecm.blog = new blog();
  return eXo.ecm.blog;
  //-------------------------------------------------------------------------//
})(gj, sharethis);