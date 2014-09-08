$(document).ready(function() {
//lazy load post comments

  $("body").lazyScrollLoading({
    lazyItemSelector: ".lazyItem",
    onScrollToBottom: function (e, $lazyItems, $firstVisibleLazyItems) {
      var viewMore = $("#view-more");

      var postPath = $('#postPath').val();
      var limit = viewMore.find('input[name="limit"]').val();
      var offset = viewMore.find('input[name="offset"]').val();
      var ws = $("#ws").val()
      var repo = $("#repo").val()
      var isAdmin = $("#isAdmin").val();
      var viewer = viewMore.find('input[name="viewer"]').val();
      var fme = viewMore.find('input[name="fme"]').val();
      var isLoad = viewMore.find('input[name="isLoad"]').val();
      var postUUID = viewMore.find('input[name="postUuid"]').val();

      if (!isLoad) return;

      var obj = new Object();
      obj.jcrPath = postPath;
      obj.limit = limit;
      obj.offset = offset + limit;
      obj.ws = ws;
      obj.repo = repo;

      $.ajax({
        url: "/portal/rest/blog/service/getComments",
        data: obj,
        dataType: "text",
        type: "POST"
      })
          .success(function (data) {
            var _result = $.parseJSON(data);
            var _data = _result.data;

            if (_result.success && _data.length > 0) {

              var blog_icon_delete = $("#blog-icon-delete").val();
              var blog_icon_edit = $("#blog-icon-edit").val();
              var blog_icon_approve = $("#blog-icon-approve").val();
              var blog_icon_disapprove = $("#blog-icon-disapprove").val();
              var blog_icon_reply = $("#blog-icon-reply").val();
              var blog_message_delete = $("#blog-message.delete").val();
              var blog_message_comment_placeholder = $("#blog-message-comment-placeholder").val();
              var result = "";
              $.each(_data, function (key, val) {
                var commentContent = val.commentContent;
                var commentDate = val.commentDate;
                var strCommentDate = getBlogTime(new Date(commentDate));
                var workspace = val.workspace;
                var commentStatus = val.commentStatus;
                var commentor = val.commentor;
                var commentPath = val.commentPath;

                result += "<li class=\"commentItem lazyItem\" id=\"comment-" + commentDate + "\">";
                result += "							<div class=\"commentLeft\">";
                result += "								<a class=\"avatarXSmall\" href=\"/portal/intranet/profile/" + commentor + "\" rel=\"tooltip\" data-placement=\"bottom\" data-original-title=\"" + fme + "\">";
                result += "									<img alt=\"" + fme + "\" src=\"/social-resources/skin/images/ShareImages/UserAvtDefault.png\">";
                result += "								</a>";
                result += "							</div><!--end commentLeft-->";
                result += "							<div class=\"commentRight\">";
                result += "								<div class=\"author\">";
                if (eval(isAdmin)) {
                  result += "									<span id=\"approve-" + commentDate + "\" class=\"pull-right approve\">";
                  result += "											<button data-placement=\"bottom\" rel=\"tooltip\" data-toggle=\"tooltip\" data-original-title=\"" + blog_icon_disapprove + "\" type=\"button\" class=\"btn\" onclick=\"eXo.ecm.blog.changeStatus(" + commentDate + ", '" + commentPath + "', '" + commentPath + "', '" + workspace + "');\">";
                  result += "												<i class=\"uiIconAnsDisapprove uiIconAnsLightGray\"></i>" + blog_icon_disapprove + "</button>";
                  result += "									</span>";
                }
                result += "									<a href=\"/portal/intranet/profile/" + viewer + "\">" + fme + "</a>";
                result += "									<span class=\"dateTime\">" + strCommentDate + "</span> &nbsp; &nbsp;";
                result += "									<input name=\"cmtPath\" type=\"hidden\" value=\"" + commentPath + "\">";
                result += "					      <input name=\"viewerFullname\" type=\"hidden\" value=\"" + fme + "\" />";
                result += "									<input name=\"ws\" type=\"hidden\" value=\"collaboration\">";
                result += "									<input name=\"avatar\" type=\"hidden\" value=\"/social-resources/skin/images/ShareImages/UserAvtDefault.png\">";
                result += "									<input name=\"fme\" type=\"hidden\" value=\"" + fme + "\">";
                result += "									<input name=\"viewer\" type=\"hidden\" value=\"" + viewer + "\">";
                result += "									<input name=\"commentor\" type=\"hidden\" value=\"" + commentor + "\">";
                result += "									<input name=\"isAdmin\" type=\"hidden\" value=\"" + isAdmin + "\">";
                result += "									<span class=\"reply actionIcon\" onclick=\"eXo.ecm.blog.replyComment(" + commentDate + ")\"><i class=\"uiIconReply uiIconLightGray\"></i>" + blog_icon_reply + "</span>";
                result += "								</div> <!--end author-->";
                result += "								<div class=\"contentComment\">";
                result += "									<span id=\"" + commentDate + "\" class=\"ContentBlock comment-context\">" + commentContent;
                result += "									</span>	 &nbsp; &nbsp;";
                if (eval(isAdmin)) {
                  result += "									<span>";
                  result += "										<a data-placement=\"bottom\" rel=\"tooltip\" data-toggle=\"tooltip\" data-original-title=\"Edit\" class=\"actionIcon\" href=\"javascript:void(0);\" onclick=\"eXo.ecm.blog.loadToEdit('" + commentPath + "', '" + postUUID + "', '" + commentDate + "', '" + workspace + "')\">";
                  result += "											<i class=\"uiIconLightGray uiIconEdit\"></i>";
                  result += "										</a>";
                  result += "										<a data-placement=\"bottom\" rel=\"tooltip\" data-toggle=\"tooltip\" data-original-title=\"Delete\" class=\"actionIcon\" href=\"javascript:void(0);\" onclick=\"eXo.ecm.blog.deleteComment('" + commentPath + "', '" + commentDate + "' ,'" + workspace + "')\">";
                  result += "											<i class=\"uiIconLightGray uiIconDelete\"></i>";
                  result += "										</a>";
                  result += "										</span>";
                }
                result += "								</div>";
                result += "							</div><!--end commentRight-->";

                // check & get child
                var obj = new Object();
                obj.jcrPath = commentPath;
                obj.limit = 5;
                obj.offset = 0;
                obj.ws = workspace;
                $.ajax({
                  url: "/portal/rest/blog/service/getComments",
                  data: obj,
                  dataType: "text",
                  async: false,
                  type: "POST"
                }) //end ajax
                    .success(function (_data) {

                      var __result = $.parseJSON(_data);
                      var __data = __result.data;
                      if (__result.success && __data.length > 0) {
                        var htmlChild = "";
                        var _totalChild = __result.total;
                        $.each(__data, function (key, val) {
                          var _commentContent = val.commentContent;
                          var _commentDate = val.commentDate;
                          var _strCommentDate = getBlogTime(new Date(commentDate));
                          var _workspace = val.workspace;
                          var _commentStatus = val.commentStatus;
                          var _commentor = val.commentor;
                          var _commentPath = val.commentPath;

                          htmlChild += "<ul class=\"commentList children\">";
                          htmlChild += "<li class=\"commentItem\" id=\"comment-" + _commentDate + "\">";
                          htmlChild += "<div class=\"clearfix comment-container\">";
                          htmlChild += "<div class=\"commentLeft\">";
                          htmlChild += "<a class=\"avatarXSmall\" href=\"/social-resources/skin/images/ShareImages/UserAvtDefault.png\" rel=\"tooltip\" data-placement=\"bottom\" data-original-title=\"" + viewer + "\">";
                          htmlChild += "<img alt=\"" + fme + "\" src=\"/social-resources/skin/images/ShareImages/UserAvtDefault.png\"></a>";
                          htmlChild += "</div>";
                          htmlChild += "<div class=\"commentRight\">";
                          htmlChild += "<div class=\"author\"><a href=\"/portal/intranet/profile/" + viewer + "\">" + fme + "</a>";
                          htmlChild += "<span class=\"dateTime\">" + _strCommentDate + "</span> &nbsp; &nbsp;";

                          htmlChild += "<input name=\"cmtPath\" type=\"hidden\" value=\"" + _commentPath + "\"/>";
                          htmlChild += "<input name=\"ws\" type=\"hidden\" value=\"" + _workspace + "\"/>";
                          htmlChild += "<input name=\"avatar\" type=\"hidden\" value=\"/social-resources/skin/images/ShareImages/UserAvtDefault.png\" />";
                          htmlChild += "<input name=\"viewer\" type=\"hidden\" value=\"" + viewer + "\"/>";
                          htmlChild += "<input name=\"fme\" type=\"hidden\" value=\"" + fme + "\"/>";
                          htmlChild += "<input name=\"isAdmin\" type=\"hidden\" value=\"" + isAdmin + "\" />";
                          htmlChild += "<input name=\"viewerFullname\" type=\"hidden\" value=\"" + fme + "\" />";
                          if (eval(isAdmin)) {
                            htmlChild += "<span id=\"approve-" + _commentDate + "\" class=\"pull-right approve\">	";
                            htmlChild += "<button type=\"button\" class=\"btn\" onclick=\"eXo.ecm.blog.changeStatus(" + commentDate + ", '" + commentContent + "', '" + _commentDate + "', 'collaboration');\" value=\"" + blog_icon_disapprove + "\">";
                            htmlChild += "<i class=\"uiIconAnsDisapprove uiIconAnsLightGray\"></i>" + blog_icon_disapprove + "</button> </span>	";
                          }
                          htmlChild += "<div class=\"contentComment\">	  <span class=\"ContentBlock\" id=\"" + _commentDate + "\">" + _commentContent + "</span>";
                          htmlChild += "<span><a data-placement=\"bottom\" rel=\"tooltip\" data-toggle=\"tooltip\" data-original-title=\"" + blog_icon_edit + "\" class=\"actionIcon\" href=\"javascript:void(0);\" onclick=\"eXo.ecm.blog.loadToEdit('" + _commentPath + "', '" + commentDate + "', '" + _commentDate + "', '" + _workspace + "')\"><i class=\"uiIconLightGray uiIconEdit\"></i></a>";
                          htmlChild += "<a data-placement=\"bottom\" rel=\"tooltip\" data-toggle=\"tooltip\" data-original-title=\"" + viewer + "\" class=\"actionIcon\" href=\"javascript:void(0);\" onclick=\"eXo.ecm.blog.deleteComment('" + _commentPath + "', '" + _commentDate + "' ,'" + workspace + "')\"><i class=\"uiIconLightGray uiIconDelete\"></i></a>";
                          htmlChild += "</span>	</div></div></div></div></li>";
                          if (key === __data.length - 1) {
                            if (_totalChild > 5) {
                              htmlChild += "<li id=\"reply-comment-more-" + commentDate + "\" onclick=\"eXo.ecm.blog.loadReply(this);\">";
                              htmlChild += "<input name=\"postUuid\" type=\"hidden\" value=\"" + postUUID + "\">";
                              htmlChild += "<input name=\"cmtPath\" type=\"hidden\" value=\"" + commentPath + "\">";
                              htmlChild += "<input name=\"isOwner\" type=\"hidden\" value=\"true\">";
                              htmlChild += "<input name=\"avatar\" type=\"hidden\" value=\"/social-resources/skin/images/ShareImages/UserAvtDefault.png\">";
                              htmlChild += "<input name=\"viewer\" type=\"hidden\" value=\"" + viewer + "\">";
                              htmlChild += "<input name=\"fme\" type=\"hidden\" value=\"" + fme + "\">";
                              htmlChild += "<input name=\"viewerFullname\" type=\"hidden\" value=\"" + fme + "\">";
                              htmlChild += "<input name=\"commentor\" type=\"hidden\" value=\"" + fme + "\">";
                              htmlChild += "<input name=\"limit\" type=\"hidden\" value=\"5\">";
                              htmlChild += "<input name=\"offset\" type=\"hidden\" value=\"0\">";
                              htmlChild += "<input name=\"isLoad\" type=\"hidden\" value=\"true\">";
                              htmlChild += "<div class=\"reply-comment-more\"><a href=\"javascript:void(0)\">Read more... </a></div>";
                              htmlChild += "</li>";
                            }
                          }
                          htmlChild += "</ul>";
                        }) //end each data


                        result += htmlChild;
                      } //end if has data
                    }) // end ajax success

                result += "						</li>";
              })

              viewMore.find('input[name="offset"]').val(eval(limit) + eval(offset));
              $("#view-more").before(result);
            } else {
              viewMore.find('input[name="isLoad"]').val(false);
              $("#view-more").find(".view-more").html('No more entry!');
            }
          })
          .error(function () {
            location.reload();
            console.log('error');
          })


    }
  });
});
