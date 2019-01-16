function loadMenu(callback){
	ajaxCall("/action/ccms/xhome/loadmenu",{
		method:"get",
		progress:false,
		dataType:"script",
		success:function(){	
			if( undefined != callback ){
				callback();
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
	if( $(".selftpwd") ){
		$(".selftpwd").unbind().on("click",function(){
			ccms.dialog.open({ url : contextPath + "/action/ccms/xhome/newpass/form", height : 400});
		});
	}
	
	loadMenu(function(){
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

		// 悬浮菜单
		ajaxCall("/action/ccms/xhome/loadsuspmenu",{
			method:"get",
			progress:false,
			dataType:"script",
			success:function(){
			}
		});
	});
	
	//判断是否是微信登陆 
	if($Mobile.isWeiXin()){
		if($Wechat.isWechat()){
			$Wechat.hideOptioMenu(); 
			$Wechat.hideToolbar();
		}else{
			if(document.addEventListener){
		        document.addEventListener('WeixinJSBridgeReady', function(){
		        	$Wechat.hideOptioMenu(); 
					$Wechat.hideToolbar();
		        }, false);
		    }else if(document.attachEvent){
		        document.attachEvent('WeixinJSBridgeReady', function(){
		        	$Wechat.hideOptioMenu(); 
					$Wechat.hideToolbar();
		        }); 
		        document.attachEvent('onWeixinJSBridgeReady', function(){
		        	$Wechat.hideOptioMenu(); 
					$Wechat.hideToolbar();
		        });
		    }
		}
	}
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