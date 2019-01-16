(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var obthis = this;
		$('.askQuestion').unbind().on("click",function(){
			gotoPage('/action/ccms/qa/web/add_question');;
		});
		this.ckfocus = function() {
			if(('<p>请写入您的答案</p>' == CKEDITOR.instances.answerCk.getData().trim()) || CKEDITOR.instances.answerCk.getData().trim()==""){
				CKEDITOR.instances.answerCk.setData("");
				return;
			}
		};

		if($('#currentId').val()!=''){
		   	 CKEDITOR.replace('answerCk');
			 CKEDITOR.instances["answerCk"].on("instanceReady", function () {  
			        this.document.on("click", obthis.ckfocus());  
			   });
		}
		
		$('#commitAnswer').unbind().on("click",function(){//添加答案
			if('<p>请写入您的答案</p>' == CKEDITOR.instances.answerCk.getData().trim() || CKEDITOR.instances.answerCk.getData().trim()==""){
				$("#answerHelp").show();
				return;
			}
			$('#commitAnswer').html("<i class='icon-spinner icon-spin'/> 保存中");
			$('#commitAnswer').attr('disabled', true);
			ccms.util.postData("/action/ccms/qa/web/q_and_a/insert/answer",{data:"qid="+$('#qid').val()+"&content="+CKEDITOR.instances.answerCk.getData().trim(),success:function(){
				//location.reload();
				gotoPage("/action/ccms/qa/web/q_and_a?qid="+$('#qid').val()+"&random="+Math.random());
			}});
		});
			
		$('#qdelete').unbind().on("click",function(){//问题删除 
			ccms.dialog.confirm("确定删除该记录？",function(){
				var qid = $('#qid').val();
				ccms.util.postData("/action/ccms/qa/web/q_and_a/delete/question",{data:"qid="+$('#qid').val(),success:function(){
					location.reload();
				}});
			});
		});
		
		//赞
		$('.ding').unbind().on("click",function() {
			var ding = $(this);
			$(ding).attr('disabled', true);
			ccms.util.postData("/action/ccms/qa/web/q_and_a/vote/up",{data:"qid="+$(this).attr('data-id')+"&qtype="+$(this).attr('data-type'),success:function(data){
				$(ding).attr('disabled', false);
				if (data.resultcode==1) {
					if(data.result==0){
						ccms.dialog.alert("不能重复操作！");
					}else {
						ding.find('span.ss').html(data.vote_count+"");
					}
				} else {
					ccms.dialog.alert(data.message);
				}
			}});
		});
		
			//踩
		$('.cai').unbind().on("click",function() {
			var cai = $(this);
			$(cai).attr('disabled', true);
			ccms.util.postData("/action/ccms/qa/web/q_and_a/vote/down",{data:"qid="+$(this).attr('data-id')+"&qtype="+$(this).attr('data-type'),success:function(data){
				$(cai).attr('disabled', false);
				if (data.resultcode==1) {
					if(data.result==0){ccms.dialog.alert("不能重复操作！");}
					else {
						cai.parent().find('span.ss').html(data.vote_count+"");
					}
				} else {
					ccms.dialog.alert(data.message);
				}
			}});
		});

		$('#commentq').unbind().on("click",function() {
			ccms.util.postData("${def:context}/action/ccms/qa/web/q_and_a/comments/add",{data:"pid="+$('#pqid').val()+"&content="+$('#ckeditorq').val(),success:function(data){
				$('#comments').empty();
				for(var i =0; i<data.length;i++){
				$('#comments').prepend("<input type='hidden' value="+data[i].commentId+"/>"+data[i].content+"<span style=' float:right'>评论于"+data[i].createdAt+"</span><br/>");
				};
				$('#ckeditorq').val('');
			}});
		});
			
			//删除提示信息  删除答案
		$(".delete").unbind().on("click",function() {
			var obthis=this;
			ccms.dialog.confirm("你确认要删除吗？", function(){
				var id= $(obthis).attr('data-id');
				ccms.util.postData("/action/ccms/qa/web/q_and_a/delete/answer?pid=" +id,{success:function(data){
					$('#answer'+id).remove();
				   	$('#qa_answer_count').html($('div.answer').length);
					$('#answerWrite').show();
					$('#answerTip').hide();
					$('#answerline'+id).hide();
				}});
			});
		});

		//采纳答案
		$(".na").unbind().on("click",function() {
			//判 断是不是问题的创建者
			ccms.util.postData("/action/ccms/qa/web/q_and_a/besta?id=" + $(this).attr("data-id"),{data:"qid="+$('#qid').val(),success:function(data){
				//location.reload();
				gotoPage("/action/ccms/qa/web/q_and_a?qid="+$('#qid').val()+"&random="+Math.random());
			}});
		});

		//收藏
		$(".favorite").unbind().on("click",function() {
			var temp = $(this);
			var type=$(this).attr("data-type");
			var id=$(this).attr("data-id");
			var url='';
			if(type=='u'){
				url="/action/ccms/qa/web/q_and_a/favorite/follwer?qid="+id+"&qtype=Q";
			}else{
				url="/action/ccms/qa/web/q_and_a/favorite/unfollwer?qid="+id+"&qtype=Q";
			}
			ccms.util.postData(url,{success:function(data){
				if (data.resultcode==1) {
					$('#followerCount_id').html(data.favorites_count+"");
					if(type=='u'){
						$(temp).addClass('faved');
						$(temp).html('已收藏');
						$(temp).attr("data-type",'f');
					}else{
						$(temp).removeClass('faved');
						$(temp).html('&nbsp;&nbsp;收藏');
						$(temp).attr("data-type",'u');
					}
				} else {
					ccms.dialog.alert(data.message);
				}
			}});
		});

		
		/*$(".unfollwer").unbind().on("click",function() {
			var temp = $(this);
			ccms.util.postData("/qa/question/favorite/" + $(this).attr("data-id") + "/unfollwer",{success:function(data){
				if (data.resultcode==1) {
					temp.parent().hide();
					$('#followerCount_id').html(data.data);
					if($(".favorite").length==0){
						$('.unfollwer').parent().parent().prepend("<h3><a href='javascript:;' class='favorite' data-id='"+temp.attr('data-id')+"'>关注</a></h3>");
					}else{
						$(".favorite").parent().show();
					}
				} else {
					ccms.dialog.alert(data.message);
				}
			}});
		});
		$(".favorite").unbind().on("click",function() {
			var temp = $(this);
			ccms.util.postData("/qa/question/favorite/" + $(this).attr("data-id") + "/follwer",{success:function(data){
				if (data.resultcode==1) {
					temp.parent().hide();
					$('#followerCount_id').html(data.data);
					$(".unfollwer").parent().show();
				} else {
					ccms.dialog.alert(data.message);
				}
			}});
		});*/

		$(".userf").unbind().on("click",function() {
			var temp = $(this);
			ccms.util.postData("/user/social/" + $(this).attr("data-id") + "/follwers",{success:function(data){
				if (data.resultcode==1) {
					temp.hide(500);
					ccms.dialog.alert(data.message);
				} else {
					ccms.dialog.alert(data.message);
				}
			}});
		});

		$(".userunf").unbind().on("click",function() {
			var temp = $(this);
			ccms.util.postData("/user/social/" + $(this).attr("data-id") + "/unFollwers",{success:function(data){
				if (data.resultcode==1) {
					temp.hide(500);
					ccms.dialog.alert(data.message);
				} else {
					ccms.dialog.alert(data.message);
				}
			}});
		});
	    //答案
		
		this.initCommentPro = function(){
			$('.showCommentBtn').unbind().on("click",function(){//显示评论列表
				var id=$(this).attr("data-id");
				$('#addCommentDiv'+id).show();
				$("#comment_contentid"+id).val('');//清空评论框
				obthis.showComment(this);
			});
			$('.comment_submit_btn').unbind().on("click",function(){//提示评论
				obthis.submitComment(this);
			});
			$('.comment_content_input').focus(function(){
					removemsgdiv(this);
			});
			$('.comment_content_input').blur(function(){
				if($(this).val()=='写下您的评论'){
					$(this).val('写下您的评论');
					initErrorMsg(this,'请输入评论');
				}
			});
		};
		this.initCommentPro();
		
		$('.question_edit').unbind().on("click",function(){//问题编辑
			var id=$(this).attr("data-id");
			gotoPage("/action/ccms/qa/web/update_question?qid="+id+"&random="+Math.random());

			//window.location.href=contextPath+'/qa/question/'+id+'/edit';
		});

		$('.answer_edit').unbind().on("click",function(){//答案编辑
			var id=$(this).attr("data-id");
			var qid=$(this).attr("data-qid");
			gotoPage("/action/ccms/qa/web/update_answer?aid="+id+"&qid="+qid+"&random="+Math.random());
			//window.location.href=contextPath+'/qa/question/answers/'+id+'/edit';
		});
		
		 $('time').timeago();
		
		this.showComment = function(ob){//显示评论
			var id=$(ob).attr("data-id");
			$('#commentlist'+id).toggle();
			if($('#commentlist'+id+" > dd.jiejue").length==0){
				$('#commentlist'+id).show();
			}
			obthis.showCommentList(id);
		};
		
		this.submitComment = function(ob){//添加评论
			var id=$(ob).attr("data-id");
			var type=$(ob).data("type");
			var qtype=type=="q"?"Q":"C";
			var inputid='comment_contentid'+id;
			if(type=='q' && ($('#'+inputid).val()=='写下您的评论' || $('#'+inputid).val()=='')){
				initErrorMsg($('#'+inputid),'请输入评论');
				$('#'+inputid).focus();
				return;
			}else if(type=='c' && ($('#'+inputid).val()=='写下您的评论' || $('#'+inputid).val()=='')){
				initErrorMsg($('#'+inputid),'请输入评论');
				$('#'+inputid).focus();
				return;
			}
			if($('#comment_submit_btn'+id).html()=='回复'){
				$('#comment_submit_btn'+id).html('添加评论');
			}
			$(ob).attr("disabled",true);
			$('#comment_submit_btn'+id).html("<i class='icon-spinner icon-spin'/> 保存中");
			ccms.util.postData("/action/ccms/qa/web/q_and_a/comments/add",{data:"pid="+id+"&content="+$('#'+inputid).val(),success:function(data){
				$('#'+inputid).val('');
				$(ob).attr("disabled",false); 
				$('#comment_submit_btn'+id).html('添加评论');
				
				if (data.resultcode==1) {
					obthis.showCommentList(id);
					obthis.showcommentCount(id,'plus');
				}else{
					ccms.dialog.alert(data.message);
				}
			}});
		};
		this.showCommentList = function(id){//显示评论
			ccms.util.postData("/action/ccms/qa/web/q_and_a/search/comments",{data:"qid="+id,success:function(data){
				$('#commentlist'+id).empty();
				data=data.answerList;
				for(var i =0; i<data.length;i++){
					var createdate=data[i].createdAt;
					var oldTime = new Date(createdate);
					var d1 = oldTime.getFullYear();
					var d2 = oldTime.getMonth()+1;
					var d3 = oldTime.getDate();
					var d4 = oldTime.getHours();
					var d5 = oldTime.getMinutes();
					var d6 = oldTime.getSeconds();
					var newTime =createdate;
					var content=data[i].content;
					var commentId=data[i].tuid;
					var name=data[i].name;
					var createdBy=data[i].createdBy;
					var cuid=$('#currentId').val();//当前用户
					commentId=parseInt(commentId);
					var timestr="<time class='timeago' datetime='"+newTime+"'/>";
					var html='<dd class="jiejue" id="commentDiv'+commentId+'">'+content+'- <a href="javascript:;" class="userCard" data-id="'+createdBy+'">'+name+'</a>  '+timestr;

					if(createdBy!=undefined){
						if(cuid==createdBy){
							html=html+'<img src="'+contextPath+'/images/qa/xg.png" title="修改" class="showeditComment" data-id="'+id+'" data-commentid="'+commentId+'"  data-content="'+content+'"  /><img title="删除" src="'+contextPath+'/images/qa/dot15.png" class="delComment" data-id="'+id+'" data-commentid="'+commentId+'"  data-content="'+content+'" data-name="'+name+'") />';
						}else{
							html=html+'<a href="javascript:;" class="huifu replyComment"     data-id="'+id+'" data-commentid="'+commentId+'"  data-content="'+content+'" data-name="'+name+'">回复</a></dd>';
						}
						html=html+'</dd>';
						$('#commentlist'+id).prepend(html);
						$('time').timeago();
					}
				};
				
				$(".jiejue").unbind().bind('click',function(e){
					var obj=$(e.target);
					var id = obj.data("id");
					var commentId = obj.data("commentid");
					var content = obj.data("content");
					var name = obj.data("name")
					if(obj.hasClass("showeditComment")){
						$(".add_conment_div").css("display","none");
						var html='<div id="editCommentDiv'+commentId+'" style=clear:both;margin-top:10px;>';
						html=html+'<dd class="tw"><input class="form-control textBlur" type="text" id="comment_contentid'+commentId+'"  value='+content+'  data-content='+content+'  /></dd>';
						html=html+'<dd class="button xx pull-right" style="margin:10px 0px"><button type=button class="editCommentBtn btn"  data-id='+commentId+'  data-pid='+id+' >修改评论</button>&nbsp;&nbsp;<button class="canelCommentBtn btn" type=button data-id='+commentId+'  >取消</button></dd>';
						html=html+'</div>';
						$('#commentDiv'+commentId).hide();
						$('#commentDiv'+commentId).after(html);
						obthis.initUpdateComment();
					}if(obj.hasClass("delComment")){
							ccms.dialog.confirm("确认删除来自"+name+"的评论吗?", function() {
								ccms.util.postData("/action/ccms/qa/web/q_and_a/comments/delete?id="+commentId,{success:function(data){
									obthis.showCommentList(id);
									obthis.showcommentCount(id,'down');
								}});
							});
					}if(obj.hasClass("replyComment")){
						$('#comment_contentid'+id).val('@'+name+" ");
						$('#comment_contentid'+id).focus();
						$('#comment_submit_btn'+id).html('回复');
					}
					obthis.textBlur();
				});
			}});
		};
		this.textBlur = function(){
			$(".textBlur").blur(function(){
				var obj = $(this);
				var content = obj.data("content");
				if (obj.val() == "") {
					obj.val(content);
				}
			});
		};
		this.showcommentCount = function(id,type){//显示评论数
			var count=parseInt($('#comment_count'+id).html());
			if(type =='plus'){
				$('#comment_count'+id).html(count+1);
			}else{
				$('#comment_count'+id).html(count-1);
			}
		};
		this.editBlurfun = function(ob,content){
			if (ob.value == "") {
				ob.value =content;
			}
		};
		this.initUpdateComment = function(){//初使化修改评论参数
			$('.canelCommentBtn').unbind().on("click",function(){
				var commentId=$(this).attr("data-id");
				
				$('#commentDiv'+commentId).show();
				$('#editCommentDiv'+commentId).remove();
			});
			$('.editCommentBtn').unbind().on("click",function(){
				var commentId=$(this).attr("data-id");
				var pid=$(this).attr("data-pid");
				var content=$('#comment_contentid'+commentId).val();
				
				$(this).attr("disabled",true); 
				var url='/action/ccms/qa/web/q_and_a/comments/update?id='+commentId;
				ccms.util.postData(url,{data:"content="+content,success:function(data){
					obthis.showCommentList(pid);
				}});
			});
		};
};
$Q_init.prototype = $Q.fn;
window["ccms"]["QandA"] = $Q;
})();

$(document).ready(function() {
	//ccms.QandA();
});
