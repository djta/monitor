(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var flag = $("#rid").val();
		if(flag == "h"){
			$(".h").addClass("select");
			document.title='最热问题';
		}else  if(flag == "n"){
			$(".n").addClass("select");
			document.title='最新问题';
		}else  if(flag == "w"){
			$(".w").addClass("select");
			document.title='待解决';
		}else  if(flag == "m"){
			$(".m").addClass("select");
			document.title='内部问题';
		}else if(flag=="open"){
			$(".open").addClass("select");
			document.title="我提出的问题";
		}else if(flag=="all"){
			$(".all").addClass("select");
			document.title="我关注的问题";
		}else if(flag=="me"){
			$(".me").addClass("select");
			document.title="我解答的问题";
		}else if(flag=="pl"){
			$(".pl").addClass("select");
			document.title="我评论的问题";
		}else if(flag=="tz"){
			$(".tz").addClass("select");
			document.title="我的通知";
		}else{
			$(".a").addClass("select");
			document.title='全部问题';
		}
		if($("#rid").val()){
			ccms.allQuestion().setPage($("#pageNo").val(),$("#count").val(),contextPath+"/qa/question/"+$("#rid").val()+"?");
		}else{
			ccms.allQuestion().setPage($('#pageNo').val(),$('#count').val(),contextPath+"/qa/question?"); 
		}
		$('time').timeago();
	};
	$Q_init.prototype = $Q.fn;
	window["ccms"]["allQ"] = $Q;
})();

$(document).ready(function() {
	ccms.allQ();
});


