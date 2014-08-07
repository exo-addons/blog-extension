(function(ecm_bootstrap, gj, wcm_utils) {
	function blog() {
	};
	blog.prototype.test = function() {
		alert('Blog\'s js worked! ');
	};

	eXo.ecm.blog = new blog();
	
	return eXo.ecm.blog;
	//-------------------------------------------------------------------------//
})(gj);
