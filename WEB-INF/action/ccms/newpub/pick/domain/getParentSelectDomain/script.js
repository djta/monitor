
var obj = document.getElementById("${fld:select_id}");
if(obj){
	if("${fld:parent_domain_value}" != ""){
		var c=obj.options.length;
		for(var i=0;i<c;i++){
			if(obj.options[i].value == "${fld:parent_domain_value}"){
				obj.options[i].selected = true;
				break;
			}
		}
		
		var p_namespace = "${fld:p_namespace}";
		if(p_namespace != ""){
			getParentSelectDomain("${fld:p_namespace}","${fld:parent_namespace}","${fld:parent_domain_value}","${fld:p_select_id}","","");
		}
	}
}
