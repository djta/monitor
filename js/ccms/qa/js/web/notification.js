(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		
		
		this.searchObjQuestions=null;
		this.searchObjTags=null;
		this.searchObjBang=null;
		this.obj=null;
		var obthis=this;
		
		this.searchObjQuestions = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/notification/search",
				rowpackage:function(obj){
					if(obj.hasRead)
						obj.op='<a href="javascript:void(0);" data-id="'+obj.tuid+'" class="read btn">知道了</a>';
					end

				 },
				success : function() {

				}
			}).initSearchBtn().searchData(1);
		
		$(".dele").unbind().on("click",function(){
			var xiao = $(this).parents(".xiaoxi");
			var deleteId = $(this).attr('data-id');
			var uri = contextPath + "/action/ccms/qa/web/notification/delete?tuid="+deleteId;
			return ajaxCall(uri,{
		        method: "get",
		        progress: true,
		        dataType: "script",
		        success: function(data) {
		        	xiao.remove();
		        }
		    });
		});
		
		$('.read').unbind().on("click",function(){
			var readbtn = $(this);
			var uri = contextPath + "/action/ccms/qa/web/notification/read?tuid="+$(this).attr('data-id');
			return ajaxCall(uri,{
		        method: "get",
		        progress: true,
		        dataType: "script",
		        success: function(data) {
		        	readbtn.hide();
		        }
		    });
		});
		
		$('.hulue').unbind().on("click",function(){
			var uri = contextPath + "/action/ccms/qa/web/notification/readall";
			return ajaxCall(uri,{
		        method: "get",
		        progress: true,
		        dataType: "script",
		        success: function(data) {
		        	$('.read').hide();
		        }
		    });
		});
		
		$('.backJsp').unbind().on("click",function(){
			ccms.dialog.close();
		});
		
		// 替换通知中a的href值
		for(var i=0;i<$('a[target=_blank]').length;i++){
			var oldUrl = $('a[target=_blank]')[i].href.toString();
			var newUrl = oldUrl.replace('/ctx','/ccms');
			$('a[target=_blank]')[i].href=newUrl;
		}
		
		$('time').timeago();
	};
		$Q_init.prototype = $Q.fn;
		window["ccms"]["notification"] = $Q;
	})();
	$(document).ready(function() {
		ccms.notification();
	});
