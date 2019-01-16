var obj = $("#${fld:field_id}");
if(obj){	var text = obj.find("option:first").text();
	obj.find("option").remove();
	$("<option value=\"\">" + text + "</option>").appendTo(obj);
	<rows>
	$("<option value=\"${fld:domain_value}\">${fld:domain_text}</option>").appendTo(obj);
	</rows>
}
