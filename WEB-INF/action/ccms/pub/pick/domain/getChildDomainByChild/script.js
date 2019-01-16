var obj = document.getElementById("${fld:select_id}");
var parent_domain_value = "";
if(obj){
	obj.options.length = 0;
	var option = new Option("","");
	obj.options.add(option);
	<rows>
		option = new Option("${fld:domain_text}","${fld:domain_value}");
		obj.options.add(option);
		parent_domain_value = "${fld:parent_domain_value}";
	</rows>
	
	setComboValue("${fld:parent_select_id}",parent_domain_value,"formEditor");
	setComboValue("${fld:select_id}","${fld:domain_value}","formEditor");
}	