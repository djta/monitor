var $menu = $("#menuTree");
$menu.empty();	// 清空悬浮菜单div
$menu.append('<div id="showMenu"><span style="color:white;font-size:1.3em;"><img style="height: 25px;width: 60px;border-radius: 4px;" src="${def:context}/images/menu.png" /></span></div>');
var maxgrade = 0, idx = 0, menustr = '', $summenu, subnum = 0, clsname = '';
<menu-rows>
	//设置当前菜单最高层级数
	if( Number("${fld:menu_grade}") > maxgrade ){
		maxgrade = Number("${fld:menu_grade}");
	}
	// 判断菜单元素所在ul是否存在
	$summenu = $menu.find("ul[grade=${fld:menu_grade}][code=${fld:pid}]");
	if( $summenu.length <= 0 ){
		clsname = ("${fld:menu_grade}" == "1" ? "navul" : "new-nav-tab");
		$menu.append('<ul class="' + clsname + '" grade="${fld:menu_grade}" code="${fld:pid}"></ul>');
		$summenu = $menu.find("ul[grade=${fld:menu_grade}][code=${fld:pid}]");
		subnum = 0;
	}else{
		subnum = $summenu.find("li").length;
	}
	if( "${fld:menu_grade}" == "1" ){	// 一级菜单
		var menu = '<li class="nav-li nav" code="${fld:menu_id}" idx="' + subnum + '" addtabs="susp_${fld:menu_id}" title="${fld:menu_name@js}" url="${fld:uri@js}">';
		if("" != "${fld:icon_path}"){
			menu += '<div class="menu-main"<img src="${def:context}${fld:icon_path}" /><br>';
		}else{
			menu += '<div class="min-menu-main">';
		}
		menu += '<a href="javascript:void(0);">${fld:menu_name@js}</a>';
		menu += '</div></li>';
		$summenu.append(menu);
	}else{	// 二级、三级菜单
		$summenu.append('<li class="nav" code="${fld:menu_id}" idx="' + subnum + '" addtabs="susp_${fld:menu_id}" title="${fld:menu_name@js}" url="${fld:uri@js}">' 
				+ '<a href="javascript:void(0);">${fld:menu_name@js}</a></li>');
	}
</menu-rows>

/** 事件绑定 */
$("#showMenu").unbind().on("mouseover click", function(){	// 显示一级菜单
	$("#menuTree").find("ul[grade=1]").show();
	$("#showMenu").hide();
});
// 子菜单显示
$(".nav").unbind().on("mouseover", function(){
	var code = $(this).attr("code");
	var grade = $(this).parent().attr("grade");
	if( undefined != grade && "" != grade ){
		grade = Number(grade) + 1;
		hideMenuUl(grade);
		var subobj = $("#menuTree").find("ul[grade=" + grade + "][code=" + code + "]");
		//var mtop = $(this).parent().css('margin-top');
		var mtop = $(this).parent().css('margin-top');
		mtop = (undefined != mtop && "" != mtop && "auto" != mtop ? mtop.replace('px','') : 0);
		mtop = Number(mtop==0?(-10):mtop) + (Number($(this).attr("idx")))*($(this).height()*(grade-1==1?1:1.8)) + (Number($(this).attr("idx")))*(-10);
		subobj.css('margin-top', mtop + 'px');
		subobj.show();
	}
});
// 子菜单隐藏
$("#menuTree").on("mouseleave", function(){
	hideMenuUl(1);
	$("#showMenu").show();
});
$("#menuTree").find("ul").on("mouseleave", function(){
	var grade = $(this).attr("grade");
	if( undefined != grade && "" != grade ){
		if( grade == maxgrade ){
			$(this).hide();
		}else{
			grade = Number(grade) + 1;
			$("#menuTree").find("ul[grade=" + grade + "][code=" + $(this).attr("code") + "]").hide();
			hideMenuUl(Number(grade) + 1);
		}
	}
});
// 菜单界面地址绑定
$("#menuTree").find(".nav").each(function(i,ele){
	if( undefined == $(this).attr("url") ||  "" == $(this).attr("url") ){
		$(this).removeAttr("url");
		$(this).removeAttr("addtabs");
		$(this).removeAttr("title");
	}else{
		$(this).attr('url',$Base64.encode($(this).attr("url")));
	}
});

function hideMenuUl(grade){
	for(var i = grade; i <= maxgrade; i++ ){
		$("#menuTree").find("ul[grade=" + i + "]").hide();
	}
}

window.onscroll=function(){
	if($("#showMenu").is(":hidden")){
		return;
	}
	var scrolltop=document.documentElement.scrollTop||document.body.scrollTop;
	startMove(parseInt(scrolltop)+18);
}

var timer=null;
var speed=0;
function startMove(target){
	var ob=document.getElementById("menuTree");
	clearInterval(timer);				//清除定时器，确保每次只有唯一定时器运行
	timer=setInterval(function(){
		var dis=target-ob.offsetTop;
		if(dis>0){
			speed=Math.ceil(dis/3);
		}
		else if(dis<0){
			speed=Math.floor(dis/3)
		}
		else{
		clearInterval(timer);
		}
	ob.style.top=ob.offsetTop+speed+'px';
	},30);
}