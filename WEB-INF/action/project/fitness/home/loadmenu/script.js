if( "" != '${fld:menustr@js}' ){
	var menuArr = JSON.parse('${fld:menustr@js}');
	var menuStr = "";
	for(var i = 0; i < menuArr.length; i++){
		var menu = menuArr[i];
		if( null == menu || "" == menu ){
			continue;
		}
		menuStr += '<dl class="am-accordion-item">';
		if( menu.hasOwnProperty("uri") && null != menu["uri"] && "" != menu["uri"] && "null" != menu["uri"] ){
			menuStr += '<dt class="am-accordion-title menu" addtabs="" title="' + menu["menu_name"] + '" url="' + menu["uri"] + '">';
		}else{
			menuStr += '<dt class="am-accordion-title">';
		}
		if( menu.hasOwnProperty("icon_path") && null != menu["icon_path"] && "" != menu["icon_path"] && "null" != menu["icon_path"] ){
			menuStr += '<img src="' + contextPath + menu["icon_path"] + '" alt="" />';
		}
		if( menu.hasOwnProperty("icon_path2") && null != menu["icon_path2"] && "" != menu["icon_path2"] && "null" != menu["icon_path2"] ){
			menuStr += '<img src="' + contextPath + menu["icon_path2"] + '" alt="" />';
		}
		menuStr += '<span>' + menu["menu_name"] + '</span>';
		menuStr += '</dt>';
		menuStr += '<dd class="am-accordion-bd am-collapse ">';
		menuStr += '<div class="am-accordion-content">';
		if( menu.hasOwnProperty("submenu") && null != menu["submenu"] && "" != menu["submenu"] && "null" != menu["submenu"] ){
			var submenuArr = JSON.parse(menu["submenu"]);
			for(var j = 0; j < submenuArr.length; j++){
				var submenu = submenuArr[j];
				if( null == submenu || "" == submenu ){
					continue;
				}
				if( submenu.hasOwnProperty("uri") && null != submenu["uri"] && "" != submenu["uri"] && "null" != submenu["uri"] ){
					var menuUri = submenu["uri"];
					if( menuUri.indexOf("?") > 0 ){
						menuUri += "&menuid=" + submenu["menu_id"];
					}else{
						menuUri += "?menuid=" + submenu["menu_id"];
					}
					menuStr += '<p class="menu" addtabs="" title="' + submenu["menu_name"] + '" url="' + menuUri + '">' + submenu["menu_name"] + '</p>';
				}else{
					menuStr += '<p>' + submenu["menu_name"] + '</p>';
				}
			}
		}
		menuStr += '</div>';
		menuStr += '</dd>';
		menuStr += '</dl>';
	}
	$("#menuList").find("dl").not(".first-bar").remove();
	$("#menuList").append(menuStr);

}