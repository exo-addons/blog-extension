(function(ecm_bootstrap, gj, wcm_utils) {
	function blog() {
	};
	blog.prototype.test = function() {
		alert('Blog\'s js worked! ');
	};


    blog.prototype.loadBlogs = function(el, year, month) {
            gj.ajax({
                url: "/portal/rest/blog-extension/service/get-blogs?year=" + year + "&month=" + month,
                dataType: "text",
                type: "POST"
            })
                .success(function (data) {
                    var _blogs = gj.parseJSON(data);
                    var html = "";
                    gj.each(_blogs, function (key, val) {
                        console.log(val.postTitle);
                        html += "<div> <a href='' >" + val.postTitle + "</a></div>";
                    });
                    gj(".blog-archive-post-link").html('');
                    gj("#month-" + year + "-" + month).html(html);
                })
        }
        eXo.ecm.blog = new blog();
	
	return eXo.ecm.blog;
	//-------------------------------------------------------------------------//
})(gj);
