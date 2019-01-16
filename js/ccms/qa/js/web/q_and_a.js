(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		
		this.searchObjQuestions=null;
		this.searchObjAnswer=null;
		this.searchObjAnswerTip=null;
		this.searchObjTips=null;
		this.obj=null;
		var obthis=this;
		
		this.searchObjQuestions = $Search({
				datagrid : "questiongrid",
				formId : "searchForm",
				path  : "/q_and_a/search/questions",
				rowpackage:function(obj){
					if(obj.userlogin != obj.createdby){
						if(obj.followercount==0)
							obj.isf='<a href="javascript:void(0);" class="post-fav tip-pop favorite" data-tip="w:关注并收藏" data-id="'+obj.tuid+'" data-type="u">&nbsp;&nbsp;收藏</a>';
						else
							obj.isf='<a href="javascript:void(0);" class="post-fav tip-pop faved favorite" data-tip="w:关注并收藏" data-id="'+obj.tuid+'" data-type="f">已收藏</a>';
						
					}
					
					if(obj.answercount==0)
						obj.answers="0";
					else
						obj.answers=obj.answercount;
					

					if(obj.userlogin!="")
						obj.comments='<a href="javascript:void(0);" class="showCommentBtn" data-id="'+obj.tuid+'" >评论(<span class=" " id="'+obj.tuid+'">'+obj.commentcount+'</span>)</a>';
					else 
						obj.comments='<a href="javascript:void(0);">评论(<span class=" " id="'+obj.tuid+'">'+obj.commentcount+'</span>)</a>';
					
					if(obj.userlogin == obj.created_by){
						obj.op='<a href="#" class="xiugai question_edit" data-id="'+obj.tuid+'">修改</a>	<span id="qdelete"><a style="cursor: pointer;" data-id="'+obj.tuid+'">删除</a></span>';
					}

					var tags = obj.tags.split(',');
					var str = "";
					for (var n=0; n<tags.length; n++){
						str += "<a href=\"javascript:gotoPage('/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+tags[n]+"');\">"+tags[n]+"</a>";	
					}
					obj.tag = str;

				 },
				success : function() {

				}
			}).initSearchBtn().searchData(1);
		
		this.searchObjAnswer = $Search({
			datagrid : "answergrid",
			formId : "searchForm",
			path  : "/q_and_a/search/answers",
			rowpackage:function(obj){
				
				if(obj.best_answer_id!=obj.tuid){
					if(obj.qcreated_by!=obj.userlogin){
						obj.cai='<span class="cai_answer"> <a href="javascript:void(0);" class="na" data-id="'+obj.tuid+'">采纳</a></span>';
					}
					
				}else{
					obj.cai='<span> <img src="'+contextPath+'/images/qa/tick-icon.png"	title="已采纳 "></img></span>';
				}
				

				if(obj.userlogin!=""){
					 obj.commentbtn='class="showCommentBtn"	data-id="'+obj.tuid+'"';
				}
				

				if(obj.userlogin==obj.created_by){
					obj.op='<a href="javascript:void(0);" class="xiugai answer_edit" data-id="'+obj.tuid+'" data-qid="' + obj.parent_id + '">修改</a>	<span class="delete" data-id="'+obj.tuid+'"><a	style="cursor: pointer;">删除</a></span>';
				}
				

				if(obj.userlogin!=""){
					obj.commentadd ="";
					obj.commentadd+='<div class="add_conment_div" style="display: none"';
					obj.commentadd+='	id="addCommentDiv'+obj.tuid+'">';
					obj.commentadd+='	<div class="tw" style="margin-top: 10px">';
					obj.commentadd+='		<input type="text" value="写下您的评论"';
					obj.commentadd+='			onfocus="if(this.value==\'写下您的评论\'){this.value=\'\';}"';
					obj.commentadd+='			onblur="if(this.value==\'\'){this.value=\'写下您的评论\'}"';
					obj.commentadd+='			class="comment_content_input form-control"';
					obj.commentadd+='			id="comment_contentid'+obj.tuid+'"></input>';
					obj.commentadd+='	</div>';
					obj.commentadd+='	<div class="button pull-right" style="margin: 10px 0px">';
					obj.commentadd+='		<button type=button class="comment_submit_btn btn"';
					obj.commentadd+='			data-id="'+obj.tuid+'" data-type="c"';
					obj.commentadd+='			id="comment_submit_btn'+obj.tuid+'">添加评论</button>';
					obj.commentadd+='	</div>';
					obj.commentadd+='</div>';
				}
				

			 },
			success : function() {

			}
		}).initSearchBtn().searchData(1);

		this.searchObjAnswerTip = $Search({
			datagrid : "answertipgrid",
			formId : "searchForm",
			path  : "/q_and_a/search/tip/answer",
			rowpackage:function(obj){
				if(obj.answer_count==0)
					obj.show_title='<p id="answerTip" style="display:none">这个问题您已经提交过答案, 您可以对现有答案进行修改</p>';
				else
					obj.show_title='<p id="answerTip">这个问题您已经提交过答案, 您可以对现有答案进行修改</p>';
				

				obj.show_input="";
				if(obj.answer_count>0){
					obj.show_input+='	style="display:none"';
				}
				else{
					obj.show_input+='  ';
				}

			 },
			success : function() {
			}
		}).initSearchBtn().searchData(1);

		this.searchObjRightQuestions = $Search({
			datagrid : "rightgrid",
			formId : "searchForm",
			path  : "/q_and_a/search/questions",
			rowpackage:function(obj){
			 },
			success : function() {

			}
		}).initSearchBtn().searchData(1);
			
		this.searchObjTips = $Search({
			datagrid : "tipsgrid",
			formId : "searchForm",
			path  : "/q_and_a/search/tip/tips",
			rowpackage:function(obj){
			 },
			success : function() {

			}
		}).initSearchBtn().searchData(1);
		
	};
	$Q_init.prototype = $Q.fn;
	window["ccms"]["q_and_a"] = $Q;
})();

$(document).ready(function() {
	ccms.q_and_a();
	setTimeout("ccms.QandA()", 1000);
});
