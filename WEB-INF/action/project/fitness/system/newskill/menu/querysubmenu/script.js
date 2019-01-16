var robj = $("#menusetting").find(".right");
var g3menu;
var str = '';

/** 二级菜单 */
<g2-rows>
str = '<div class="r-0">';

str += '<div data-grade="2" data-pid="${fld:pid}" data-rootid="${fld:pid}">';
str += '<p data-code="${fld:tuid}">';
str += '<input type="checkbox" name="menu_id" data-pid="${fld:pid}" data-rootid="${fld:pid}" data-grade="2" value="${fld:tuid}" />${fld:menu_name@js}';
str += '</p>';
str += '</div>';

str += '<div data-grade="3" data-pid="${fld:tuid}" data-rootid="${fld:pid}"></div>';
if( Number("${fld:subcount}") <= 0 ){
	str += '<div data-grade="4" data-pid="${fld:tuid}"><p data-grade="btn" data-menuid="${fld:tuid}"></p></div>';
}else{
	str += '<div data-grade="4" data-pid="${fld:tuid}"></div>';
}

str += '</div>';

robj.append(str);
</g2-rows>
renderCheckboxObj("input[data-grade=2]");

/** 三级菜单 */
<g3-rows>
g3menu = robj.find("div[data-grade=3][data-pid=${fld:pid}]");
g4menu = robj.find("div[data-grade=4][data-pid=${fld:pid}]");
if( g3menu.length > 0 ){
	str = '<p>';
	str += '<input type="checkbox" name="menu_id" data-pid="${fld:pid}" data-rootid="${fld:rootid}" data-grade="3" value="${fld:tuid}" />${fld:menu_name@js}';
	str += '</p>';
	g3menu.append(str);
	g4menu.append('<p data-grade="btn" data-menuid="${fld:tuid}"></p>');
}
</g3-rows>
renderCheckboxObj("input[data-grade=3]");

/** 加载菜单按钮 */
<btn-rows>
$("p[data-grade=btn][data-menuid=${fld:menu_id}]").append('<input type="checkbox" name="menu_btn_id" data-menuid="${fld:menu_id}" data-code="${fld:tuid}" data-grade="4" value="${fld:tuid}" />${fld:btn_name@js}');
</btn-rows>
ccms.util.renderCheckbox("menu_btn_id");

/** 加载菜单权限 */
<menuskill-rows>
setCheckboxRadioValue("menu_id", "${fld:menu_id}");
</menuskill-rows>

/** 加载按钮权限 */
<btnskill-rows>
setCheckboxRadioValue("menu_btn_id", "${fld:menu_btn_id}");
</btnskill-rows>

/** 事件绑定 */
$("input[name=menu_id][data-grade=1]").unbind().on("ifClicked",function(){
	var rootid = $(this).attr("data-rootid");
	if( $(this).prop("checked") ){
		$("input[name=menu_id][data-rootid="+rootid+"]").iCheck('uncheck');
	}else{
		$("input[name=menu_id][data-rootid="+rootid+"]").iCheck('check');
	}
});

ischange = false;
$("input[name=menu_id],input[name=menu_btn_id]").on("ifClicked",function(){
	ischange = true;
});
