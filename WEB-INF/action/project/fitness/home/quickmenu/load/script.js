var quickstr = '', quicknum = 0;
<quick-rows>
	quicknum++;
	quickstr = '<figure class="quickmenu" code="${fld:menu_id}" addtabs="quick_${fld:menu_id}_'+quicknum+'" title="${fld:menu_name@js}" url="'+$Base64.encode(escape("${fld:uri}?menuid=${fld:tuid}"))+'">';
	quickstr += '<img src="${def:context}${fld:icon_path@js}" title="${fld:menu_name@js}"/>';
	quickstr += '<figcaption>${fld:menu_name@js}</figcaption>';
	quickstr += '</figure>';
	$("#quickmenu_btn").after(quickstr);
</quick-rows>

$(".quickmenu").unbind().click(function(e){
	var thisobj = $(this);
	e.preventDefault();
	thisobj.unbind();
	thisobj.addtabs();
	thisobj.click();
});
