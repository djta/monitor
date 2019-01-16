(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var obthis = this;
		this.init = function() {
		},
		this.isLogin=function(){//验证登录
			if(obthis.valiateLogin()){
				return true;
			}
			return false;
		},this.valiateLogin=function(){//验证登录
			var user= $.cookie('access_token');
			if(user!='' && user!="" && user!='""' && $.trim(user).length>0){
				return true;
			}
			return false;
		},this.selectSearch=function(selectId){
			obthis.selectId=selectId;
			$('#' + selectId).select2({
				placeholder : "搜索问题,标签,用户",
				minimumInputLength : 1,
				width : 260,
				ajax : {
					url : contextPath + "/qa/question/search.json",
					dataType : 'json',
					data : function(term, page) {
						return {
							term : term,
							page_limit : 15
						};
					},
					results : function(data, page) {
						return {
							results : data.result
						};
					}
				},
				formatResult : obthis.movieFormatResult,
				formatSelection : movieFormatSelection,
				dropdownCssClass : "bigdrop",
				escapeMarkup : function(m) {
					return m;
				}
			});
			
			$("#"+selectId).on("select2-selecting", function(e) {
				if(e.object.type==1){
					window.location.href=contextPath+"/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+e.object.title+"";
				}
				/*if(e.object.type==0){
					window.location.href="/u/"+e.object.domainName;
				}*/
				if(e.object.type==2){
					window.location.href=contextPath+"/qa/question/"+e.object.id;
				}
			});
		},
		this.movieFormatResult=function(movie) {
			   var title ="";
				   if(movie.type==1){
					   if(movie.avatarUrl==null){ 
						   title +=""+movie.title+"&nbsp;&nbsp;问题数："+movie.number;
				      }else{
				    	  title +="<img style='height:20px;width:20px;' src='/images/qa/"+movie.avatarUrl+"'>"+movie.title+"&nbsp;&nbsp;问题数："+movie.number;
				      }
					  
				   };
				   /*if(movie.type==0){
					   if(movie.avatarUrl==null){ 
						   title +=""+movie.title;
				      }else{	  
				    	  title +="<img style='height:20px;width:20px;' src='/image/"+movie.avatarUrl+"'>"+movie.title;
				      }
				   };*/
				   if(movie.type==2){
					   title +=""+movie.title+"&nbsp;&nbsp答案："+movie.number;
				   }

		    	 return title;
		},this.movieFormatSelection=function(movie) {
		    return "";
		}
		
	};

	$Q_init.prototype = $Q.fn;
	window["ccms"]["qa"] = $Q;
})();

$(document).ready(function() {
	ccms.qa().init();
});
