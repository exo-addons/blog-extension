(function (gj) {
    function blog() {
    };

    blog.prototype.test = function () {
        alert('Blog\'s js worked! ');
    };

    blog.prototype.getPost = function (el, year, month) {
        gj.ajax({
            url: "/portal/rest/blog-extension/service/get-blogs?year=" + year + "&month=" + month,
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

    blog.prototype.blogArchiveAccordionInit = function(){
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
    blog.prototype.syncuri = function(dateTime) {
        var name = gj("#name");
        var data = name.val();
        data = dateTime + "/" + data;
        gj('#uri').replaceWith('<span id="uri">'+data+'</span>');
    }

    gj("#title").change(function() {
        var name = gj("#name");
        if (!name.readOnly) {
            var title = this.value;
            var portalContext = eXo.env.portal.context;
            var portalRest = eXo.env.portal.rest;
            var url = portalContext+"/"+portalRest+"/l11n/cleanName";
            gj.ajax({
                type: "GET",
                url: url,
                data: { name: title},
                success: function(data) {
                    gj('#name').val(data).trigger('change');
                }
            }); // end ajax
        } // end if not readonly
    }); // end change title
    gj("#name").change(blog.prototype.syncuri);


    blog.prototype.postComment = function(uuid) {
        var aform = gj("#commentform-"+uuid);
        var comment = aform.find( 'input[name="comment"]' ).val();
        var path = aform.find( 'input[name="jcrPath"]' ).val();
        gj.ajax({
            type: "POST",
            url: "/rest/contents/comment/add",
            data: { comment: comment, jcrPath:path},
            success: function() {
                gj('#respond-$uuid').html("<div id='message'></div>");
                gj('#message').html('<div class="alert alert-success"><p>Your comment has been posted.</p></div>')
                    .hide()
                    .fadeIn(1500);
            }
        }); // end ajax

        return false;
    }; // end click on button


    eXo.ecm.blog = new blog();
    return eXo.ecm.blog;
    //-------------------------------------------------------------------------//
})(gj);