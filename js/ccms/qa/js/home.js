
/*
*页面跳转，flag表示url是否加密，默认为false
*/

function loadDivPage(id,url,flag){
	if(url==undefined || url=="") return;
	if(flag == undefined){
		flag = false;
	}
	var hash = flag?url:$Base64.encode(url);
	var tabObj = $("#menuTab a[href=\"#"+hash+"\"]");
	if(tabObj.length > 0){
		$(".menu_on").removeClass("menu_on");
		tabObj.addClass("menu_on");
	}
	ajaxLoad($("#"+id), contextPath+(flag?$Base64.decode(url):url),function(){
		/**
		* 页面初始化时给必填字段初始化
		*/
		$("#"+id+" label.required").each(function(){
			if($(this).find(".red").length == 0){
				$(this).append("<span class='red'>*</span>");
			}
		});
		//radio样式
		$('#'+id+' input[type=radio]').iCheck({
			   radioClass: 'iradio_square-blue',
			   increaseArea: '20%'
		});
		 //checkbox样式
		$('#'+id+' input[type=checkbox]').iCheck({
				checkboxClass: 'icheckbox_square-blue',
				increaseArea: '20%'
		});
		$("#"+id+" .col_main_body_title").prepend($("<button>").addClass("btn btn-default pull-left btn-goto-back").html("<span class='glyphicon glyphicon-arrow-left'></span>"));
		$("#"+id+" .btn-goto-back").css("margin-top", "-5px").unbind().on("click",function(){
			gotoBack();
		});
		/*if($HomeHash != hash){
		}*/
	});
}
/*
*页面返回
*/
function gotoBack(){
	window.history.back();
}
function loadMenu(){
	var url=contextPath+"/action/ccms/home/menu";
	ajaxCall(url,{
		method: "GET",
		progress: true,
		dataType: "json",
		success: function(data) {
			var tenantry=data["tenantry"];
			$("#home_tenantry_name").html(tenantry);
			var menuArray=data["data"];
			menuArray.pop();
			$("#menuTab").empty();
			if($Mobile.any()){
				$(".home_head").hide();
				$(".home_main_body").css("top","0px");
				$(".home_main_body").css("bottom","0px");
				$(".home_container_box").css("padding-right","0px");
				$("#home_col_side").hide();
				$(".home_mod-footer").hide();
				$("#home_gotoCon_btn").hide();
				$("#home_col_child").hide();
				//右边
				var html='<a href="javascript:void(0);" title="返回" ><span class="glyphicon glyphicon-th pull-right home_menubtn" id="home_menu_btn"></span><span class="glyphicon glyphicon-arrow-left pull-right home_menubtn" id="home_gotoCon_btn" style="display:none;"></span></a>';
				$(".home_main_body").after(html);
				$("#menuTab").before("<div class='text-center' id='home_menu_title' ><a href='"+contextPath+"/action/security/exit' title='退出' ><span class='glyphicon glyphicon-tag pull-left home_exitbtn' id='home_exit_btn'></span></a><p>系统菜单</p></div>");
				$("#menuChildTab").before("<div class='text-center' id='home_menu_title' ><p>系统菜单</p></div>");
				$("#home_col_side").addClass("col-xs-12 col-sm-12 col-md-12 col-lg-12 home_col_side_any").removeClass("home_col_side");
				//$("#home_menu_btn").unbind();
				$("#home_menu_btn").unbind().on("click",function(){
					//判断列表是否隐藏
					if(!$("#body_content").is(":hidden")){
						$(".home_menubtn").css("color","rgb(66, 139, 202)");
						$("#body_content").hide();
						$("#home_col_side").slideDown("slow");
						$("#home_col_child").hide();
						$("#home_gotoCon_btn").hide();
					}else{
						$("#home_col_side").hide();
						$("#body_content").slideDown("slow");
						$(".home_menubtn").css("color","#000000");
						$("#home_col_child").hide();
						$("#home_gotoCon_btn").hide();
					}
				});
				$("#home_gotoCon_btn").hide(); 
				//$("#home_gotoCon_btn").unbind();  
				$("#home_gotoCon_btn").unbind().on("click",function(){
					//判断一级菜单是否隐藏
					if($("#home_col_side").is(":hidden") && !$("#home_col_child").is(":hidden")){
						$(".home_menubtn").css("color","rgb(66, 139, 202)");
						$("#body_content").hide();
						$("#home_col_side").slideDown("slow");
						$("#home_col_child").hide();
						$("#home_gotoCon_btn").hide();
					}else{
						gotoBack();
					}
				});
				
				for(var i=0;i<menuArray.length;i++){
					var obj=menuArray[i];
					var pid=obj.pid;
					var title=obj.title;
					var id=obj.id;
					var html='<li class="menu" style="clear:both;text-align:center" ><a class="menu_title_any" value="'+i+'" >'+title+'</a></li>';
					var childHtml='<ul style="display:none" class="child_menu_any" id="childMenu'+i+'"><ul>';
					if(pid==0){
						$("#menuTab").append(html);
						$("#menuChildTab").append(childHtml);
						for(var j=0;j<menuArray.length;j++){
							var obj2=menuArray[j];
							var pid2=obj2.pid;
							var title2=obj2.title;
							var path=$Base64.encode(obj2.path);
							var logopath=obj2.logopath;
							if(id==pid2){
								var imagehtml='';
								if(logopath!=''){
									imagehtml='<img src="'+logopath+'" class="logoimg" />';
								}else{
									imagehtml='';
								}
								var html2='<li class="menu"><a href="#'+path+'" class="cmenu_title_any">'+imagehtml+title2+'</a></li>';
								$("#childMenu"+i).append(html2);
							}
						}
					}
				}
				//一级菜单单击事件
				$(".menu_title_any").click(function(){
					$("#home_gotoCon_btn").show();
					$(".child_menu_any").hide();
					$("#home_col_child").slideDown("slow");
					$("#childMenu"+$(this).attr("value")).show();
					$("#home_col_side").hide();
				});
				//二级菜单单击事件
				$(".child_menu_any a").click(function (e) { 
					e.preventDefault();
					if(e.target.hash != undefined){
						$("#home_gotoCon_btn").hide();
						$(".home_menubtn").css("color","#000000");
						$("#home_col_side").hide();
						$("#home_col_child").hide();
						$("#body_content").slideDown("slow");
						window.location.hash = e.target.hash;
					}
				});
			}else{
				$(".home_head").show();
				$(".home_col_side").show();
				$(".home_mod-footer").show();
				$(".home_main_body").css("top","66px");
				$(".home_main_body").css("bottom","30px");
				$(".home_container_box").css("padding-right","20px");
				for(var i=0;i<menuArray.length;i++){
					var obj=menuArray[i];
					var pid=obj.pid;
					var title=obj.title;
					var id=obj.id;
					var html='<li class="home_menu" style="clear:both;text-align:center" ><a class="menu_title">'+title+'<i class="icon_menu glyphicon glyphicon-chevron-down" ></i></a><ul style="display:none" class="child_menu" id="childMenu'+i+'"><ul></li>';
					if(pid==0){
						$("#menuTab").append(html);
						for(var j=0;j<menuArray.length;j++){
							var obj2=menuArray[j];
							var pid2=obj2.pid;
							var title2=obj2.title;
							var path=$Base64.encode(obj2.path);
							var logopath=obj2.logopath;
							if(id==pid2){
								var imagehtml='';
								if(logopath!=''){
									imagehtml='<img src="'+logopath+'" class="logoimg" />';
								}else{
									imagehtml='';
								}
								var html2='<li class="menu"><a href="#'+path+'" class="cmenu_title">'+imagehtml+title2+'</a></li>';
								$("#childMenu"+i).append(html2);
							}
						}
					}
				}
				$(".menu_title").click(function(){
					if($(this).find("i").hasClass("glyphicon-chevron-up")){
						$(this).next().hide();
						$(this).find("i").removeClass("glyphicon-chevron-up").addClass("glyphicon-chevron-down");
					}else{
						$(".child_menu").hide();
						$("#menuTab").find(".glyphicon").removeClass("glyphicon-chevron-up").addClass("glyphicon-chevron-down");
						$(this).next().show();
						$(this).find("i").removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-up");
					}
				});
				$(".child_menu a").click(function (e) { 
					e.preventDefault();
					if(e.target.hash != undefined){
						window.location.hash = e.target.hash;
					}
				});
			}
		}
	});
}

//定义首界面hash
var $HomeHash = "";
$(document).ready(function() {
	//判断是否是微信登陆 
	loadMenu();
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
	//ccms.dialog.notice("hashchange: " + hash);
	var actionroot=$("#home_actionroot").val();
	if(hash == ""){
		loadDivPage("body_content",actionroot+"/home/panel");
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