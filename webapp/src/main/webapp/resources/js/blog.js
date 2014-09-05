(function () {
  function blog() {
  };

  blog.prototype.getPost = function (el, year, month) {
    $.ajax({
      url: "/portal/rest/blog/service/get-blogs?year=" + year + "&month=" + month,
      dataType: "text",
      type: "POST"
    })
        .success(function (data) {
          var _blogs = $.parseJSON(data);
          var html = "";
          $.each(_blogs, function (key, val) {
            var link = "/portal/intranet/blog/article?content-id=/repository/collaboration" + val.postPath;
            html += "<div> <a href='" + link + "' >" + val.postTitle + "</a></div>";
          });
          $(".blog-archive-post-link").html('');
          $("#month-" + year + "-" + month).html(html);
        })
  }

  $.fn.blogArchiveAccordion = function (options) {
    if (this.length > 1) {
      this.each(function () {
        $(this).blogArchiveAccordion(options);
      });
      return this;
    }
    var settings = $.extend({
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
        var childs = $(elem).children();
        $(childs[0]).addClass('acc_head');
        $(childs[1]).addClass('acc_content');
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
        var s_parent = $(this).parent();

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
            $(this).children('.uiIconLightGray').addClass('uiIconArrowDown');
          }else{
            s_parent.children('.acc_content').slideUp(settings.slideSpeed);
            s_parent.removeClass('acc_active');
            s_parent.children('.uiIconLightGray').removeClass('uiIconArrowDown');
            $(this).children('.uiIconLightGray').addClass('uiIconArrowRight');
          }
        }
        else {
          $(this).next('.acc_content').slideDown(settings.slideSpeed);
          s_parent.addClass('acc_active');
          $(this).children('.uiIconLightGray').removeClass('uiIconArrowRight');
          $(this).children('.uiIconLightGray').addClass('uiIconArrowDown');
        }
      });
    }

    init();
    return this;
  };

  //postform
  blog.prototype.syncuri = function (dateTime) {
    var name = $("#name");
    var data = name.val();
    data = dateTime + "/" + data;
    $('#uri').replaceWith('<span id="uri">' + data + '</span>');
  }

  $("#title").change(function () {
    var name = $("#name");
    if (!name.readOnly) {
      var title = this.value;
      var portalContext = eXo.env.portal.context;
      var portalRest = eXo.env.portal.rest;
      var url = portalContext + "/" + portalRest + "/l11n/cleanName";
      $.ajax({
        type: "GET",
        url: url,
        data: { name: title},
        success: function (data) {
          $('#name').val(data).trigger('change');
        }
      }); // end ajax
    } // end if not readonly
  }); // end change title
  $("#name").change(blog.prototype.syncuri);

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
    var aform = $("#commentform-" + uuid);
    var comment = aform.find('input[name="comment"]').val();
    var type = aform.find('input[name="type"]').val();
    var isAdmin = aform.find('input[name="isAdmin"]').val();

    var blog_icon_delete = $("#blog-icon-delete").val();
    var blog_icon_edit = $("#blog-icon-edit").val();
    var blog_icon_approve = $("#blog-icon-approve").val();
    var blog_icon_disapprove = $("#blog-icon-disapprove").val();
    var blog_icon_reply = $("#blog-icon-reply").val();
    var blog_message_delete = $("#blog-message.delete").val();
    var blog_message_comment_placeholder = $("#blog-message-comment-placeholder").val();

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
    $.ajax({
      type: "POST",
      url: "/rest/contents/comment/add",
      data: { comment: comment, jcrPath: path},
      success: function () {
        $.ajax({
          type: "POST",
          url: "/portal/rest/blog/service/getLastComment",
          data: {jcrPath: path},
          async:false,
          success: function (data) {
            if(data.result){
              var commentContent = data.commentContent;
              var commentPath = data.commentPath;
              var ws = data.ws;
              var totalComment = $("#totalCurrentComment").val();//data.totalComment;
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
              var result="";
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
              result += "<input name=\"isAdmin\" type=\"hidden\" value=\""+isAdmin+"\" />";

              result += "<input name=\"postPath\" type=\"hidden\" value=\""+postPath+"\" />";

              result += "	<span class=\"reply actionIcon\" onclick=\"eXo.ecm.blog.replyComment("+timeId+")\" ><i class=\"uiIconReply uiIconLightGray\"></i> "+blog_icon_reply+"</span>";
              if(isAdmin == "true"){
                result += " <span id=\"approve-"+timeId+"\" class=\"pull-right approve\">";
                result += "	<button type=\"button\" class=\"btn\" onclick=\"eXo.ecm.blog.changeStatus("+timeId+", '"+commentPath+"', '"+commentPath+"', '"+ws+"');\" value=\""+blog_icon_disapprove+"\">";
                result += "<i class=\"uiIconAnsDisapprove uiIconAnsLightGray\"></i>"+blog_icon_disapprove
                result += "</button>"
                result += " </span>"
              }

              result +=	"	<div class=\"contentComment\">";
              result += "	  <span class=\"ContentBlock\"  id=\""+timeId+"\">"+commentContent;
              result += "	  </span>";
              result += "	  <span>";
              result += " 		<a data-placement=\"bottom\" rel=\"tooltip\" data-toggle=\"tooltip\" data-original-title=\""+blog_icon_edit+"\"  class=\"actionIcon\" href=\"javascript:void(0);\" onclick=\"eXo.ecm.blog.loadToEdit('"+commentPath+"', '"+uuid+"', '"+timeId+"', '"+ws+"')\"><i class=\"uiIconLightGray uiIconEdit\"></i></a>";
              result += "			<a data-placement=\"bottom\" rel=\"tooltip\" data-toggle=\"tooltip\" data-original-title=\""+blog_icon_delete+"\"  class=\"actionIcon\" href=\"javascript:void(0);\" onclick=\"eXo.ecm.blog.deleteComment('"+commentPath+"', '"+timeId+"' ,'"+ws+"')\"><i class=\"uiIconLightGray uiIconDelete\"></i></a>";
              result += "		</span>";
              result += "	</div>"

              result += "</div>";
              //  result += "</div>";
              result += "</li>";

              if(type !== 'rootPostComment'){
                $("#comment-form-"+uuid+" .commentItem").has("form").remove();
                $("#comment-"+uuid).append("<ul class=\"commentList children\">"+result + "</ul>");
              }else{
                $("#commentList").append(result);
              }
              $("#commentInputBox input[name=comment]").val('');
              var plural="";
              totalComment ++ ;
              if(totalComment>1){plural="s";}
              $("#total-comment").html(totalComment + " comment" + plural + "<input type=\"hidden\" id=\"totalCurrentComment\" value=\""+totalComment+"\">");
            }else{//end if (_data.result=false)
              alert("Error, u can trial again!.");
            }
          }

        }); //end ajax get last comment
      }, // end sucess 
      error: function() {
        if(comfirm('Your session have been expired!. Would you want re-login?')){
          location.reload();
        }
      }
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
      $.ajax({
        url: "/portal/rest/blog/service/changeCommentStatus",
        dataType: "text",
        data: obj,
        type: "POST"
      })
          .success(function (data) {
            var rs = $.parseJSON(data);
            var btn = '<button type="button" onclick="eXo.ecm.blog.changeStatus(\'' + elId + '\', \'' + nodePath + '\',  \'' + postPath + '\',\''+ws+'\');" ';


            var blog_icon_approve = $("#blog-icon-approve").val();
            var blog_icon_disapprove = $("#blog-icon-disapprove").val();

            if (!rs.result) {
              btn += 'class="btn btn-primary" > <i class="uiIconAnsApprove uiIconAnsWhite"></i>'+blog_icon_approve+'</button>'
              $('#' + elId).removeClass('approved');
              $('#' + elId).addClass('disapproved');
            } else {
              btn += 'class="btn" ><i class="uiIconAnsDisapprove uiIconAnsLightGray"></i>'+blog_icon_disapprove+'</button>'
              $('#' + elId).removeClass('disapproved');
              $('#' + elId).addClass('approved');
            }

            $('#approve-' + elId).html(btn);
          })
    }
  }


  $(document).ready(function(){
    $('.rate-wrapper-display').on('click', function(){
      var blog_message_rate_title =  $('#blog-message-rate-title').val();
      var blog_message_rate_button = $('#blog-message-rate-button').val();

      var rateForm = $('.rate-wrapper-form');
      $('.UIPopupWindow').remove();
      rateForm.removeAttr('style');
      var ex = new Messi(rateForm.html(),{
        title: ''+blog_message_rate_title,
        buttons: [{id: 0, label: ''+blog_message_rate_button, val: 'Y'}],
        callback: function(val) { /*alert('Your selection: '); */}});
      rateForm.attr('style', 'display:none;');


      $('.messi-box').find('.rate-wrapper').each(function(i, item) {
        //var rateForm = $('.rate-wrapper-form');
        $(item).rate({
          uid: $(item).find('input').val(),
          on_click: function(ui, score) {
            //send out our ajax call

            var obj = new Object();
            obj.score = score;
            obj.postPath = ui.find("input[name=postPath]").val();
            obj.ws = ui.find("input[name=ws]").val()
            console.log(obj);
            $.ajax({
              url: "/portal/rest/blog/service/updateVote",
              dataType: "text",
              data: obj,
              type: "POST"
            })
                .success(function (data) {
                  var _score = score*20;
                  $('.rate-wrapper-display .rate-current-score').removeAttr('style');
                  $('.rate-wrapper-display .rate-current-score').attr('style', 'display: block; width: '+_score+' %;');
                  //$('.rate-wrapper-display .rate-current-score').removeAttr('style');

                  console.log(data);
                })

            //alert('sending out product_id: ' + p.val() + ' with score ' + score);
          },
          cookie_domain: '.mydomain.com',
          cookie_name: 'my-rating-cookie-name'
        });
      })

      $('body').append(rateForm);
    })
    $('.rate-wrapper-display').each(function(i, item) {
      $(item).rate({});
    })



  });




  //scroll
  var ScrollToTop = function (options) {
    this.$doc = $('body');
    this.options = $.extend(ScrollToTop.defaults, options);

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
    $.extend(self, {
      init: function () {
        self.transition = self.transition();
        self.build();


        if (self.options.target) {
          if (typeof self.options.target === 'number') {
            self.target = self.options.target;
          } else if (typeof self.options.target === 'string') {
            self.target = Math.floor($(self.options.target).offset().top);
          }
        } else {
          self.target = 0;
        }

        self.$trigger.on('click.scrollToTop', function () {
          self.$doc.trigger('ScrollToTop::jump');
          return false;
        });

        // bind events
        self.$doc.on('ScrollToTop::jump', function () {
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

          self.$doc.addClass(self.classes.animating);


          if (self.transition.supported) {
            var pos = $(window).scrollTop();

            self.$doc.css({
              'margin-top': -pos + self.target + 'px'
            });
            $(window).scrollTop(self.target);

            self.insertRule('.duration_' + speed + '{' + self.transition.prefix + 'transition-duration: ' + speed + 'ms;}');

            self.$doc.addClass('easing_' + easing + ' duration_' + speed).css({
              'margin-top': ''
            }).one(self.transition.end, function () {
              self.$doc.removeClass(self.classes.animating + ' easing_' + easing + ' duration_' + speed);
            });
          } else {
            $('html, body').stop(true, false).animate({
              scrollTop: self.target
            }, speed, function () {
              self.$doc.removeClass(self.classes.animating);
            });
            return;
          }
        })
            .on('ScrollToTop::show', function () {
              if (self.isShow) {
                return;
              }
              self.isShow = true;

              self.$trigger.addClass(self.classes.show);
            })
            .on('ScrollToTop::hide', function () {
              if (!self.isShow) {
                return;
              }
              self.isShow = false;
              self.$trigger.removeClass(self.classes.show);
            })
            .on('ScrollToTop::disable', function () {
              self.disabled = true;
              self.$doc.trigger('ScrollToTop::hide');
            })
            .on('ScrollToTop::enable', function () {
              self.disabled = false;
              self.toggle();
            });

        $(window).on('scroll', self._throttle(function () {
          if (self.disabled) {
            return;
          }

          self.toggle();
        }, self.options.throttle));

        if (self.options.mobile) {
          $(window).on('resize', self._throttle(function () {
            if (self.disabled) {
              return;
            }

            self.checkMobile();
          }, self.options.throttle));
        }

        self.toggle();
      },
      checkMobile: function () {
        var width = $(window).width();

        if (width < self.options.mobile.width) {
          self.useMobile = true;
        } else {
          self.useMobile = false;
        }
      },
      build: function () {
        if (self.options.trigger) {
          self.$trigger = $(self.options.trigger);
        } else {
          self.$trigger = $('<a href="#" class="' + self.classes.trigger + ' ' + self.classes.skin + '">' + self.options.text + '</a>').appendTo($('body'));
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
        if ($(window).scrollTop() > distance) {
          return true;
        } else {
          return false;
        }
      },
      toggle: function () {
        if (self.can()) {
          self.$doc.trigger('ScrollToTop::show');
        } else {
          self.$doc.trigger('ScrollToTop::hide');
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
      this.$doc.trigger('ScrollToTop::jump');
    },
    disable: function () {
      this.$doc.trigger('ScrollToTop::disable');
    },
    enable: function () {
      this.$doc.trigger('ScrollToTop::enable');
    },
    destroy: function () {
      this.$trigger.remove();
      this.$doc.data('ScrollToTop', null);
      this.$doc.off('ScrollToTop::enable')
          .off('ScrollToTop::disable')
          .off('ScrollToTop::jump')
          .off('ScrollToTop::show')
          .off('ScrollToTop::hide');
    }
  };

  $.fn.scrollToTop = function (options) {
    if (typeof options === 'string') {
      var method = options;
      var method_arguments = arguments.length > 1 ? Array.prototype.slice.call(arguments, 1) : undefined;

      return this.each(function () {
        var api = $.data(this, 'scrollToTop');

        if (api && typeof api[method] === 'function') {
          api[method].apply(api, method_arguments);
        }
      });
    } else {
      return this.each(function () {
        var api = $.data(this, 'scrollToTop');
        if (!api) {
          api = new ScrollToTop(options);
          $.data(this, 'scrollToTop', api);
        }
      });
    }
  };

  blog.prototype.initScroll = function(){
    $('body').scrollToTop();
    $( ".scrollToTop" ).attr("title", "Back to top");
  }

  //edit comment
  blog.prototype.loadToEdit = function(commentPath, postUUID, timeId, ws){
    var obj = new Object();
    obj.commentPath = commentPath;
    obj.ws = ws;
    $.ajax({
      url: "/portal/rest/blog/service/getComment",
      dataType: "text",
      data:obj,
      type: "POST"
    })
        .success(function (data) {
          var _result = $.parseJSON(data);
          if(_result.result){
            var type = $('#commentform-'+postUUID+' input[name=type]').val();
            if(type !== 'rootPostComment'){
              blog.prototype.replyComment(postUUID);
            }else{
              $('#commentform-'+postUUID+' input[name=commentCancel]').removeAttr("style");
            }
            $('#commentform-'+postUUID+' input[name=comment]').val(_result.commentContent);
            $('#commentform-'+postUUID+' input[name=comment]').focus();
            $('#commentform-'+postUUID+' input[name=commentPath]').val(_result.commentPath);
            $('#commentform-'+postUUID+' input[name=timeId]').val(timeId);
            $('#commentform-'+postUUID+' input[name=ws]').val(ws);
            $('#commentform-'+postUUID+' input[name=action]').val("Edit");
            $('#commentform-'+postUUID+' input[name=submit]').attr("value","Update Comment");
            $('#commentform-'+postUUID+' input[name=submit]').attr("onclick","eXo.ecm.blog.editComment('"+postUUID+"')");
          }else{
            alert('Comment in '+commentPath+ ' doesnt exist!');
          }
        })

  }

  blog.prototype.deleteComment = function(commentPath, commendId, ws){
    /* $.confirm({
     text: "This is a confirmation dialog manually triggered! Please confirm:",
     confirm: function(button) {
     alert("You just confirmed.");
     },
     cancel: function(button) {
     alert("You cancelled.");
     }
     });
     */
    if (confirm("Are u sure?")) {
      var obj = new Object();
      if (commentPath.charAt(1)==='/') commentPath.substr(1, commentPath.length);

      obj.commentPath = commentPath;
      obj.ws = ws;
      $.ajax({
        type: "POST",
        data:obj,
        url: "/portal/rest/blog/service/delComment",
        success: function (data) {
          if(data.result){
            //var totalComment = data.totalComment;
            var totalComment = $("#totalCurrentComment").val();//data.totalComment;
            totalComment--;
            var plural="";
            if(totalComment>1){plural="s";}
            $('#comment-'+commendId).remove();
            if(totalComment > 0){
              $("#total-comment").html(totalComment + " comment" + plural + "<input type=\"hidden\" id=\"totalCurrentComment\" value=\""+totalComment+"\">");
            }else{
              $("#total-comment").html("Comment" + plural + "<input type=\"hidden\" id=\"totalCurrentComment\" value=\""+totalComment+"\">");
            }
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
    var aform = $("#commentform-" + uuid);
    var comment = aform.find('input[name="comment"]').val();
    var path = aform.find('input[name="commentPath"]').val();
    var timeId = aform.find('input[name="timeId"]').val();
    var ws = aform.find('input[name="ws"]').val();
    var type = aform.find('input[name="type"]').val();
    var obj = new Object();
    obj.commentPath = path;
    obj.newComment = comment;
    obj.ws=ws;
    $.ajax({
      type: "POST",
      url: "/portal/rest/blog/service/editComment",
      data: obj,
      success: function (data) {
        if(data.result){
          $('#'+timeId).html(comment);
          var destination = $('#comment-'+uuid+" span .ContentBlock");

          destination.html(comment);

          if(type !== 'rootPostComment'){
            $('#commentform-'+uuid).closest("ul").remove();
          }
          $('#commentform-'+uuid+' input[name=submit]').attr("value","Post Comment");
          $('#commentform-'+uuid+' input[name=submit]').attr("id","btn-"+uuid);
          $('#commentform-'+uuid+' input[name=submit]').attr("onclick","eXo.ecm.blog.postComment('"+uuid+"')");
          $('#commentform-'+uuid+' input[name=commentCancel]').attr("style","display:none;");
          $('#commentform-'+uuid+' input[name=action').val('');
          $('#commentform-'+uuid+' input[name=comment').val('');
          /*
           $('html, body').animate({
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
      $('#commentform-'+commentId+' input[name=commentCancel]').attr("style", "display:none;");
      $('#commentform-'+commentId+' input[name=submit]').attr("value", "Post Comment");
      $('#commentform-'+commentId+' input[name=comment]').val('');
    }
    $("#comment-"+commentId+" ul:last-child ").has("form").remove();
  }
  /**
   reply a comment
   */
  blog.prototype.replyComment = function(commentId){
    var commentItem = $("#comment-"+commentId);
    var commentPath = commentItem.find('input[name="cmtPath"]').val();
//		var CPath = commentItem.find('input[name="postPath"]').val();
    var ws = commentItem.find('input[name="ws"]').val();
    var avatar = commentItem.find('input[name="avatar"]').val();
    var viewer = commentItem.find('input[name="viewer"]').val();
    var isAdmin = commentItem.find('input[name="isAdmin"]').val();
    var viewerFullname = commentItem.find('input[name="viewerFullname"]').val();
    $("#comment-"+commentId+" ul:last-child ").has("form").remove();
    var blog_message_replycomment_placeholder = $("#blog-message-replycomment-placeholder").val();
    var commentForm="";
    commentForm += "<ul class=\"commentList children\" id=\"comment-form-"+commentId+"\" >";
    commentForm += "<li class=\"commentItem\">";

    commentForm += "<div class=\"commentItem commentFormBox clearfix\">";
    commentForm += "	<div class=\"commentLeft\">";
    commentForm += "		<a data-original-title=\""+viewerFullname+"\" href=\"/portal/intranet/profile/"+viewer+"\" data-placement=\"bottom\" rel=\"tooltip\" class=\"avatarXSmall\">";
    commentForm += "			<img alt=\""+viewerFullname+"\" src=\""+avatar+"\">";
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
    commentForm += "					<input name=\"isAdmin\" type=\"hidden\" value=\""+isAdmin+"\" />";
    commentForm += "					<input name=\"viewerFullname\" type=\"hidden\" value=\""+viewerFullname+"\" />";

    commentForm += "					<input name=\"fme\" type=\"hidden\" value=\""+viewerFullname+"\" />";
    commentForm += "					<input name=\"timeId\" type=\"hidden\" value=\""+commentId+"\" />";
    commentForm += "					<input name=\"ws\" type=\"hidden\" value=\""+ws+"\" />";
    commentForm += "					<input name=\"action\" type=\"hidden\" value=\"\" />";
    commentForm += "					<input name=\"commentPath\" type=\"hidden\" value=\"/repository/collaboration"+commentPath+"\"/>";
    commentForm += "					<input name=\"jcrPath\" type=\"hidden\" value=\"/repository/collaboration"+commentPath+"\"/>";
    commentForm += "					<input type=\"text\" onkeydown=\"return eXo.ecm.blog.prePostComment(event, '"+commentId+"')\" style=\"width:100%\" tabindex=\"0\" placeholder=\""+blog_message_replycomment_placeholder+"\" id=\"comment-"+commentId+"\" name=\"comment\">";
    commentForm += "					</div>";
    commentForm += "			</form>";
    commentForm += "		</div> <!--end comment input box-->";
    commentForm += "	</div> <!--end comment right-->";
    commentForm += "</div>";
    commentForm += "</li></ul>";

    commentItem.append(commentForm);
    $("#commentform-"+commentId).find('input[name="comment"]').focus();
  }

  //context-menu in comment
  $.fn.contextPopup = function(menuData) {
    // Define default settings
    var settings = {
      contextMenuClass: 'ClickPopupContent dropdown-menu dropdownArrowTop',
      headerClass: 'header',
      seperatorClass: 'divider',
      title: '',
      items: []
    };

    // merge them
    $.extend(settings, menuData);

    // Build popup menu HTML
    function createMenu(e) {
      var xxx = $(this.parentNode); //e.toElement.parents('.commentRight').children('input[name="cmtPath"]').val();
      console.log('xxx: ' + xxx);

      $('.contextMenuPlugin').remove();
      var menu = $('<ul class="' + settings.contextMenuClass + '"><div class="' + settings.gutterLineClass + '"></div></ul>')
          .appendTo(document.body);
      if (settings.title) {
        $('<li class="' + settings.headerClass + '"></li>').text(settings.title).appendTo(menu);
      }
      settings.items.forEach(function(item) {
        if (item) {
          var rowCode = '<li><a href="#" class="actionIcon"><span></span></a></li>';
          // if(item.icon)
          //   rowCode += '<img>';
          // rowCode +=  '<span></span></a></li>';
          var row = $(rowCode).appendTo(menu);
          if(item.styleclass){
            var icon = $('<i>');
            icon.attr('class', item.styleclass);
            icon.insertBefore(row.find('span'));
          }
          row.find('span').text(item.label);

          if (item.isEnabled != undefined && !item.isEnabled()) {
            row.addClass('disabled');
          } else if (item.action) {
            row.find('a').click(function () { item.action(e); });
          }

        } else {
          $('<li class="' + settings.seperatorClass + '"></li>').appendTo(menu);
        }
      });
      menu.find('.' + settings.headerClass ).text(settings.title);
      return menu;
    }
    // On contextmenu event (right click)
    this.bind('contextmenu', function(e) {
      var menu = createMenu(e)
          .show();
      var left = e.pageX + 5, /* nudge to the right, so the pointer is covering the title */
          top = e.pageY;
      if (top + menu.height() >= $(window).height()) {
        top -= menu.height();
      }
      if (left + menu.width() >= $(window).width()) {
        left -= menu.width();
      }
      // Create and show menu
      menu.css({zIndex:1000001, left:left, top:top + 50})
          .bind('contextmenu', function() { return false; });
      // Cover rest of page with invisible div that when clicked will cancel the popup.
      var bg = $('<div></div>')
          .css({left:0, top:0, width:'100%', height:'100%', position:'absolute', zIndex:1000000})
          .appendTo(document.body)
          .bind('contextmenu click', function() {
            // If click or right click anywhere else on page: remove clean up.
            bg.remove();
            menu.remove();
            return false;
          });
      // When clicking on a link in menu: clean up (in addition to handlers on link already)
      menu.find('a').click(function() {
        bg.remove();
        menu.remove();
      });
      // Cancel event, so real browser popup doesn't appear.
      return false;
    });
    return this;
  };

  $('.comment-context').contextPopup({
    items: [
      {
        label:'Edit',
        styleclass:'uiIconLightGray uiIconEdit',
        action:function(e) {

        }
      },{
        label:'Remove',
        styleclass:'uiIconLightGray uiIconDelete',
        action:function() {
          alert('clicked 8')
        }
      }
    ]
  });
  eXo.ecm.blog = new blog();
  return eXo.ecm.blog;
  //-------------------------------------------------------------------------//
})();