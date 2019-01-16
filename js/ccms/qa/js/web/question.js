(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		
		this.searchObjQuestions=null;
		this.searchObjQuestionsCollect=null;
		this.searchObjQuestionsMe=null;
		this.searchObjQuestionsOpen=null;
		this.searchObjQuestionsPl=null;
		this.searchObjTags=null;
		this.searchObjBang=null;
		this.obj=null;
		var obthis=this;

		//所有
		if($("#qtype").val()=="" && $("#ptype").val()==""){
			this.searchObjQuestions = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/all",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}
		
		//热门
		if($("#qtype").val()=="hot" && $("#ptype").val()==""){
			this.searchObjQuestions = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/hot",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}
		
		//内部，视频
		if($("#qtype").val()=="movie" && $("#ptype").val()==""){
			this.searchObjQuestions = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/movie",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		//新贴
		if($("#qtype").val()=="new" && $("#ptype").val()==""){
			this.searchObjQuestions = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/new",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}
		
		if($("#qtype").val()=="wait" && $("#ptype").val()==""){
			this.searchObjQuestions = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/wait",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		//我提出
		if($("#qtype").val()=="" && $("#ptype").val()=="open"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/all?ptype=Q&createdby=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="hot" && $("#ptype").val()=="open"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/hot?ptype=Q&createdby=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="new" && $("#ptype").val()=="open"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/new?ptype=Q&createdby=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="movie" && $("#ptype").val()=="open"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/movie?ptype=Q&createdby=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="wait" && $("#ptype").val()=="open"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/wait?ptype=Q&createdby=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		//我收藏
		if($("#qtype").val()=="" && $("#ptype").val()=="collect"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/all?ptype=Q&collect=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="hot" && $("#ptype").val()=="collect"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/hot?ptype=Q&collect=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="new" && $("#ptype").val()=="collect"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/new?ptype=Q&collect=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="movie" && $("#ptype").val()=="collect"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/movie?ptype=Q&collect=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="wait" && $("#ptype").val()=="collect"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/wait?ptype=Q&collect=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		
		//我解答
		if($("#qtype").val()=="" && $("#ptype").val()=="me"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/all?ptype=A&createdby=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="hot" && $("#ptype").val()=="me"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/hot?ptype=A&createdby=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="new" && $("#ptype").val()=="me"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/new?ptype=A&createdby=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="movie" && $("#ptype").val()=="me"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/movie?ptype=A&createdby=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="wait" && $("#ptype").val()=="me"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/wait?ptype=A&createdby=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}
		
		//我评论
		if($("#qtype").val()=="" && $("#ptype").val()=="pl"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/all?ptype=C&createdby=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="hot" && $("#ptype").val()=="pl"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/hot?ptype=C&createdby=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="new" && $("#ptype").val()=="pl"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/new?ptype=C&createdby=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="movie" && $("#ptype").val()=="pl"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/movie?ptype=C&createdby=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}

		if($("#qtype").val()=="wait" && $("#ptype").val()=="pl"){
			this.searchObjQuestionsCollect = $Search({
				datagrid : "datagrid",
				formId : "searchForm",
				path  : "/question/search/questions/wait?ptype=C&createdby=${def:user}",
				rowpackage:function(obj){
					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;
					if(obj.answers>0){
						obj.answers_style="background-color: #46a546; border-radius: 3px; color: #fff; padding: 6px 10px; margin-right: 20px";
					}else{
						obj.answers_style="color: #9d261d; padding: 6px 10px; margin-right: 20px";
					}
				 },
				success : function() {
					$('time').timeago();
				}
			}).initSearchBtn().searchData(1);
		}
		

		this.searchObjTags = $Search({
			datagrid : "tagsgrid",
			formId : "searchForm",
			path  : "/question/search/tags/hot",
			rowpackage:function(obj){
				/**if(obj.icon_url!=""){
					obj.tagurl='<img src="/images/qa/'+obj.icon_url+'" style="width: 16px; height: 16px; margin-bottom: 2px"	alt1="'+obj.name+'" />';
				}*/
			 },
			success : function() {

			}
		}).initSearchBtn().searchData(1);

		this.searchObjBang = $Search({
			datagrid : "banggrid",
			formId : "searchForm",
			path  : "/question/search/bang",
			rowpackage:function(obj){
			 },
			success : function() {

			}
		}).initSearchBtn().searchData(1);
		
	};
	$Q_init.prototype = $Q.fn;
	window["ccms"]["question"] = $Q;
})();

$(document).ready(function() {
	ccms.question();
});
