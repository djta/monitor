/** 一级菜单 */
var classMap = {1:"", 2:"two-col", 3:"three-col", 4:"four-col"};
var ulobj = $("#menunav");
<rootmenu-rows>
	var menu = '<li class="nav-li">';
	menu += '<div class="imal">';
	if("" != "${fld:icon_path}"){
		menu += '<img src="${def:context}${fld:icon_path}" /><br>';
	}
	menu += '<a>${fld:menu_name@js}</a><a><!--span class="arrow">></span--></a></div>';
	var ulnum = Math.ceil((parseInt("${fld:mainnum}") + parseInt("${fld:subnum}"))/10);	// 向上取整
	var liclass = classMap[ulnum];	// 多页class名
	var extul = "";	// 子菜单ul扩展数量
	for( var i = 0; i < ulnum-1; i++ ){
		extul += '<ul class="xuxian"></ul>';
	}
	menu += '<div class="nav-tab ' + (undefined != liclass ? liclass : "") + '" id="div_${fld:menu_id}"><ul></ul>' + extul + '</div>';
	menu += '</li>';
	ulobj.append(menu);
</rootmenu-rows>
//菜单底部
//ulobj.append('<li class="nav-li" style="bottom:0;"><img src="${def:context}/js/ccms/xhome/image/u3.png" style="height: 125px; width: 115px;" /><br></li>');
// 首页
var homepage = '<li class="nav-li menu" addtabs="" title="首页" id="homepage" close="false" style="display:none;" url="/action/ccms/xhome/main" >';
homepage += '<a href="javascript:void(0)" class="cmenu_title subBtn firstPage">首页</a></li>';
ulobj.append(homepage);

/** 二级菜单、三级菜单 */
<submenu-rows>
	var submenu = "";
	if( "" == "${fld:uri}" && "2" == "${fld:menu_grade}" ){	// 二级粗体菜单
		submenu = '<li id="li_${fld:menu_id}" class="nav-li"><span>${fld:menu_name@js}</span></li>';
	}else{	// 二级、三级菜单
		submenu = '<li id="li_${fld:menu_id}" class="nav-li menu" addtabs="" title="${fld:menu_name@js}" url="${fld:uri}" >';
		submenu += '<a href="javascript:void(0)" class="cmenu_title subBtn">${fld:menu_name@js}</a>';
		submenu += '</li>';
	}
	
	var navlen = $("#div_${fld:root_id} ul").find(".nav-li").length;
	var ulnum = Math.ceil((navlen+1)/10);	// 向上取整
	ulnum = (ulnum > 1 ? ulnum - 1 : 0);
	$("#div_${fld:root_id} ul").eq(ulnum).append(submenu);
</submenu-rows>

