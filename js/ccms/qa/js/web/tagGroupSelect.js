(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var obthis = this;
		this.showTagControl =function(id) {//标签控件
			$('#' + id).select2({
				matcher : function(term, text) {
					return text.toUpperCase().indexOf(term.toUpperCase()) == 0;
				},
				placeholder : "如:doctor",
				minimumInputLength : 1,
				multiple : true,
				allowClear : true,
				openOnEnter : true,
				width : 200,
				ajax : {
					url : contextPath + "/action/ccms/qa/tag/taglist",
					dataType : 'json',
					type : "post",//使用get方法访问后台
					data : function(term, page) {
						return {
							term : term, // search term
							page_limit : 15
						};
					},
					results : function(data, page) { //
						var array = data.result;
						var tempArray = [];
						for (var i = 0; i < array.length; i++) {
							var obj = array[i];
							var map = {};
							obj.id = i;
							tempArray.push(obj);
						}
						return {
							results : tempArray
						};
					}
				},
				formatResult :  function(data) {
					var html = '';
					if (data.iconUrl != '' && data.iconUrl != null) {
						html = '<img src="' + contextPath + '/images/qa/' + data.iconUrl
								+ '" style="height:25px;width:25px;"/>';
					}
					html = html + data.name;
					return html;
				},
				formatSelection : function(data) {
					return data.name;
				},
				dropdownCssClass : "bigdrop",
				escapeMarkup : function(m) {
					return m;
				},
				formatInputTooShort : function(term, minLength) {
					return '添加' + term;
				},
				formatNoMatches : function(term) {
					return '添加' + term;
				},
				containerCssClass : 'myclass',
			});
		};



	};
	$Q_init.prototype = $Q.fn;
	window["ccms"]["tagGroupSelect"] = $Q;
})();

$(document).ready(function() {
	ccms.tagGroupSelect();
});


