(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var obthis = this;
		$('.askQuestion').unbind().on("click",function(){
			gotoPage('/action/ccms/qa/web/add_question');
		});
		this.unReadNotifications = function() {
			  var handler = function(){
				  ccms.util.postData("/action/ccms/qa/web/question/search/unread",{success:function(data){
						$("#tongzhiCount").html(data.result);
					}});
			    }
			    var timer = setInterval( handler , 180000);
			    var clear = function(){
			        clearInterval(timer);
			    };
		};
		$(".myTongzhi").unbind().bind("click",function(){
			var urlTZ = contextPath + '/action/ccms/qa/web/notification?random='+Math.random();
			ccms.dialog.open({url : urlTZ,width : 1100,height : 600});
		});
		this.ownData = function(){
			ccms.util.postData("/action/ccms/qa/web/question/search/answerbest",{success:function(data){
				$('#huida').empty();
				$('#caina').empty();
				$('#huida').prepend(data.rows[0].answerCount);
				$('#caina').prepend(data.rows[0].bestCount);
			}});
			//关注数
			ccms.util.postData("/action/ccms/qa/web/question/search/followcount",{success:function(data){
				$('#guanzhu').empty();
				$('#guanzhu').prepend(data.rows[0].followCount);
			}});
			//粉丝数
			ccms.util.postData("/action/ccms/qa/web/question/search/fanscount",{success:function(data){
				$('#fensi').empty();
				$('#fensi').prepend(data.rows[0].fansCount);
			}});
		};
		if( $("#allQuestionScript") && undefined != $("#allQuestionScript").attr("code") ){
			var defuser = $("#allQuestionScript").attr("code");
			if (defuser != "" ) {
				obthis.unReadNotifications();
				obthis.ownData();
			}
		};
		this.setPage = function(currentPage, totalPages, url) {
			var options = {
					bootstrapMajorVersion : 3,
					currentPage : currentPage ? currentPage : 1,
					numberOfPages : 5,
					totalPages : totalPages,
					size : 'mini',
					alignment : 'center',
					itemTexts : function(type, page, current) {
						switch (type) {
						case "first":
							return "首页";
						case "prev":
							return "上一页";
						case "next":
							return "下一页";
						case "last":
							return "尾页";
						case "page":
							return page;
						}
					},
					tooltipTitles : function(type, page, current) {
						switch (type) {
						case "first":
							return "第一页";
						case "prev":
							return "上一页";
						case "next":
							return "下一页";
						case "last":
							return "最后一页";
						case "page":
							return (page === current) ? "当前页是 " + page
									: " 去第 " + page+" 页";
						}
					}
					,
					pageUrl : function(type, page, current) {
						return url + "p=" + page;
					}
				};
				$('#page').bootstrapPaginator(options);
			};
	};
	$Q_init.prototype = $Q.fn;
	window["ccms"]["allQuestion"] = $Q;
})();

$(document).ready(function() {
	ccms.allQuestion();
});
