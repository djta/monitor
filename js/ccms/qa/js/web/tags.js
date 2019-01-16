(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		
		this.searchObjQuestions=null;
		this.searchObjQuestionsHot=null;
		var obthis=this;

		//所有
		if($("#qttype").val()==""){
			this.searchObjQuestions = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/tags/search/questions/all",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
												str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+encodeURIComponent(tags[n])+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; height: 58px; width: 56px; margin-right: 20px";
					}else{
						obj.answers_style=" color: #9d261d; padding: 6px 10px; height: 58px; width: 56px; margin-right: 20px";
					}
					
					if(obj.commentcount>0)
						obj.comments_color="color:green;";
						
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}
		
		//热门
		if($("#qttype").val()=="hot"){
			this.searchObjQuestions = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/tags/search/questions/hot",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; height: 58px; width: 56px; margin-right: 20px";
					}else{
						obj.answers_style=" color: #9d261d; padding: 6px 10px; height: 58px; width: 56px; margin-right: 20px";
					}

					if(obj.commentcount>0)
						obj.comments_color="color:green;";
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}
		
		//内部，视频
		if($("#qttype").val()=="movie"){
			this.searchObjQuestions = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/tags/search/questions/movie",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; height: 58px; width: 56px; margin-right: 20px";
					}else{
						obj.answers_style=" color: #9d261d; padding: 6px 10px; height: 58px; width: 56px; margin-right: 20px";
					}

					if(obj.commentcount>0)
						obj.comments_color="color:green;";
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		//新贴
		if($("#qttype").val()=="new"){
			this.searchObjQuestions = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/tags/search/questions/new",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; height: 58px; width: 56px; margin-right: 20px";
					}else{
						obj.answers_style=" color: #9d261d; padding: 6px 10px; height: 58px; width: 56px; margin-right: 20px";
					}

					if(obj.commentcount>0)
						obj.comments_color="color:green;";
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}
		
		if($("#qttype").val()=="wait"){
			this.searchObjQuestions = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/tags/search/questions/wait",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; height: 58px; width: 56px; margin-right: 20px";
					}else{
						obj.answers_style=" color: #9d261d; padding: 6px 10px; height: 58px; width: 56px; margin-right: 20px";
					}

					if(obj.commentcount>0)
						obj.comments_color="color:green;";
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}
		
		$(".xl span").click(function() {
			$(".xlnr").slideDown("normal");
		});
		$(".xl .xlnr li").click(function() {
			$(".xl p").html($(this).html());
			$(this).parent().css("display", "none");
		});
		if($('#tqid').val()=='q')
		{
			if($('#qttype').val()=='hot'){
				$(".xl p").html('最热的');
			}else if($('#qttype').val()=='wait'){
				$(".xl p").html('未回答的');
			}else{
				$(".xl p").html('最新的');
			}
		}else{
			if($('#qttype').val()=='hot'){
				$(".xl p").html('最热的');
			}else{
				$(".xl p").html('最新的');
			}
		}
		
		/*var url=$('#searchForm').attr('action')+'?'+$('#searchForm').serialize()+'&';
		if ($("#tqid").val()) {
			ccms.allQuestion().setPage($("#pageNo").val(), $("#count").val(), url);
		} else {
			ccms.allQuestion().setPage($('#pageNo').val(), $('#count').val(), url);
		}*/
		$("div.xl li").unbind().on("click",function() {
			var qt=$(this).attr('data-id');
			$("#qttype").val(qt);
			var url = '/action/ccms/qa/web/tag/tags'+'?'+$('#searchForm').serialize();
			gotoPage(url);
			//var  url=$('#searchForm').attr('action')+'?'+$('#searchForm').serialize();
			//window.location.href = url;
		});
		
		$('#searchTerm').keyup(function(e){
			if(e.keyCode==13){
				$('#termid').val($(this).val());
				//$('#searchForm').submit();
				var url = '/action/ccms/qa/web/tag/tags'+'?'+$('#searchForm').serialize();
				gotoPage(url);
			}
		});
		$('time').timeago();
	};
	$Q_init.prototype = $Q.fn;
	window["ccms"]["tags"] = $Q;
})();

$(document).ready(function() {
	ccms.tags();
});
