var obj = $("#${fld:id}");
if( obj ){
	var text = $("#${fld:id} option:first").text();
	$("#${fld:id} option").remove();
	$("<option value=\"\">" + text + "</option>").appendTo(obj);
	<domain-rows>
	$("<option value=\"${fld:domain_value}\">${fld:domain_text_cn}</option>").appendTo(obj);
	</domain-rows>
}