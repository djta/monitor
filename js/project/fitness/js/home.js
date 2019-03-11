function loadMenu(callback){
	getAjaxCall("/action/project/fitness/home/loadmenu", false, callback);
}

function loadRemindnum(){
	silentAjaxCall("/action/project/fitness/home/loadremindnum", {
		method : "get",
		progress : false,
		dataType : "json",
		success : function(data) {
			var msgnum = 0;
			if( undefined != data && undefined != data.msgnum ){
				msgnum = parseInt(data.msgnum);
			}
			if( msgnum > 0 ){
				$("#remindbtn").attr("count",msgnum);
				$("#remindbtn").addClass("r-hea-news-remind");
			}else{
				$("#remindbtn").attr("count","");
				$("#remindbtn").removeClass("r-hea-news-remind");
			}
		}
	});
}


//包含tab页的div加载事件
$(function(){
    $('#tabs').unbind().addtabs();
})

//定义首界面hash
var $HomeHash = "";
$(document).ready(function() {



	// 菜单
	loadMenu(function(){
		$('.changeTab').off('click').on('click', function() {
			$('.conL').toggleClass('small');
		});
		
		$(".am-accordion-title").unbind().on("click",function(){
			var obj = $(this).next("dd");
			$(this).parent().siblings().find(".am-in").removeClass("am-in");
			$(this).parent().siblings().find(".am-active").removeClass("am-active");
			if( $(this).hasClass("am-active") ){
				$(this).removeClass("am-active");
				obj.removeClass("am-in");
			}else{
				$(this).addClass("am-active");
				obj.addClass("am-in");
			}
		});
		
		$('.nav-li').hover(function(){$(this).addClass('on')},function(){$(this).removeClass('on')});
		$(".home_head").show();
		var addtabs = 100;
		$(".menu").each(function(){
			var uri = $(this).attr("url");
			if( undefined != uri && "" != uri ){
				addtabs++;
				$(this).attr("url", $Base64.encode(escape(uri)));
				$(this).attr("addtabs", addtabs);
			}
		});
		$("#homepage").click();
	});
	// tab操作
	/*$("#tabManage").find(".open-tabmanage,.taboperate").unbind().on("mouseover",function(){
		$("#tabManage .taboperate").show();
	}).on("mouseout",function(){
		$("#tabManage .taboperate").hide();
	});*/
	// 顶部下拉菜单
	$("#avatarsimg").mouseover(function(){
		$(".head-show").removeClass("disNone");
	}).mouseout(function(){
		$(".head-show").addClass("disNone");
	});
	// 退出
	$("#exit_btn").unbind().on("click",function(){
		location.href = contextPath + "/action/security/exit";
	});
	// 修改密码
	$("#update_pwd_btn").unbind().on("click",function(){
		ccms.dialog.open({ url : contextPath+"/action/project/fitness/newpass/form", height : 300,width:500});

	});
	// 修改姓名
	$("#update_name_btn").unbind().on("click",function(){
		ccms.dialog.open({ url : contextPath+"/action/project/fitness/newname/form", height : 200,width:500});
	});
	// 加载提醒消息数
/*	setInterval(function(){	// 60秒执行一次
		loadRemindnum();
	},60*1000);*/
});

$(window).on("hashchange",function(){
	var hash = window.location.hash;
	if(hash == ""){
		//loadDivPage("body_content", $("#home_actionroot").val()+"/home/panel");
	}else if (hash.match("#")) {
		var h = hash.split("#")[1];
		if($HomeHash == ""){
			$HomeHash = h;
		}
		if(h.indexOf("a_") < 0){
			loadDivPage("body_content",h,true);
		}
	}
});
$(function(){ $(window).trigger("hashchange");});