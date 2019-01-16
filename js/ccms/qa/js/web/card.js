(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var cardTop;
		var cardLeft;
		var temp = 0;
		/**
		$("body").delegate(".userCard","mouseover",function() {
			$(".card").fadeIn(300);
			var top = $(this).offset().top;//对象距离顶部的距离
			var left = $(this).offset().left;//对象距离左边距的距离
			var winWidth = $(window).width();//窗口的宽度
			var userHeadertop = top;//获取对像距离浏览器顶部距离
			//150：userCard的高度；350：userCard的宽度
			// 右上方  
			if (userHeadertop - 150 > 0 && (left< winWidth/2 || left <350)) {
				cardTop = top-120;
				cardLeft = left + 30;
			}
			// 右下方
			else if (userHeadertop - 150 < 0 && left< winWidth/2 && left<350 ) {
				cardTop = top + 30;
				cardLeft = left+20;
			}
			// 左上方
			else if (userHeadertop - 150 > 0 && (left >350 || left>winWidth/2)) {
				cardTop = top -120;
				cardLeft = left -320;
			}
			// 左下方
			else {
				cardTop = top + 50;
				cardLeft = left - 350 + 50;
			}
			$(".card").css({
				"top" : cardTop,
				"left" : cardLeft
			});
		});
		$("body").delegate(".card","mouseover",function() {
			temp = 1;
		});
		$("body").delegate(".userCard","mouseleave",function() {
			setTimeout(function() {
				if (!temp) {
					$(".card").hide();
				}
			}, 200);
		});
		$("body").delegate(".card","mouseleave",function() {
			temp = 0;
			$(".card").hide();
		});*/

		$('#foll').hide();
		$('#unfoll').hide();
		/***
		$("body").delegate(".userCard","mouseover",function() {
			var islogin=true;
			if("${def:user}"==''){
				islogin=false;
			}
			
			ccms.util.postData("/action/ccms/qa/web/question/search/answerbest?id="+$(this).attr("data-id"),{islogin:islogin,success:function(data){
				$('#nickname').empty();
				$('#follow').empty();
				$('#fans').empty();
				$('#introduction').empty();
				$('#cardImg').empty();
				$('#userAnswersId').empty();
				$('#bestAnswersId').empty();
				$('#userid').empty();
				$('#uLogin').empty();
				
				var userType = '';
				if(data.rows.userType==0){
					userType= '用户';
				}else if(data.rows.userType==1){
					userType= '医生';
				}else if(data.rows.userType==2){
					userType= '销售代表';
				}else if(data.rows.userType==3){
					userType= '管理员';
				}
				
				$('#cardImg').attr('src',contextPath+"/images/qa/"+data.rows.avatarUrl);
				$('#nickname').prepend(data.rows.userName);
				$('#userAnswersId').prepend(data.rows.answerCount);
				$('#introduction').prepend(userType);//签名
				$('#uLogin').prepend(data.rows.userlogin);//签名
				$('#bestAnswersId').prepend(data.rows.bestCount);
				$('#userid').val(data.rows.userlogin);
			}});
			ccms.util.postData('/action/ccms/qa/web/question/search/followcount?id='+$(this).attr("data-id"),{islogin:islogin,success:function(data){
				if(data.result==0){
					$('#unfoll').hide();
					$('#foll').show();
				}else{
					$('#foll').hide();
					$('#unfoll').show();
					$('#follow').prepend(data.rows.followCount);
				}
			}});

			//粉丝数
			ccms.util.postData('/action/ccms/qa/web/question/search/fanscount?id='+$(this).attr("data-id"),{islogin:islogin,success:function(data){
				$('#fans').empty();
				$('#fans').prepend(data.rows.fansCount);
			}});
			
			if($('#userLogin').val()==null || $('#userLogin').val()=='' || $(this).attr("data-id")==$('#userLogin').val()){
				$("#ziji").hide();
			}else{
				$("#ziji").show();
			}
		});*/
		//关注用户
		$('#foll').unbind().on("click",function(){
			ccms.util.postData("/action/ccms/qa/web/question/search/follwers?userid=" + $('#userid').val() ,{success:function(data){
				$('#foll').hide();
				$('#unfoll').show();
				ccms.allQuestion().ownData();
			}});
		});

		//取消关注
		$('#unfoll').unbind().on("click",function(){
			ccms.util.postData("/action/ccms/qa/web/question/search/follwers?userid=" + $('#userid').val() + "&followerid="+ $('#followerid').val(),{success:function(data){
				$('#unfoll').hide();
				$('#foll').show();
				ccms.allQuestion().ownData();
			}});
		});
		
	};
	$Q_init.prototype = $Q.fn;
	window["ccms"]["crad"] = $Q;
})();

$(document).ready(function() {
	ccms.crad();
});

