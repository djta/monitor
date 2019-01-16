clearForm("formEditor");

document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.clause_code.value = "${fld:clause_code@js}";
document.formEditor.field_type.value = "${fld:field_type@js}";
document.formEditor.clause_code_name.value = "${fld:field_name@js}";
document.formEditor.clause_filter.value = "${fld:clause_filter@js}";
document.formEditor.phrase_name.value = "${fld:phrase_name@js}";
document.formEditor.clause_value.value = "${fld:clause_value@js}";
document.formEditor.namespace.value = "${fld:namespace@js}";

setCheckboxValue("is_node","${fld:is_node@js}","formEditor");
setCheckboxValue("logic_type","${fld:logic_type@js}","formEditor");

if("${fld:filter_type@js}" == "1"){
	document.getElementById("formTitle").innerHTML = "修改活动限定人群";
}else{
	document.getElementById("formTitle").innerHTML = "修改活动排除人群";
}

changeRule("${fld:is_node@js}");

//隐藏选择节点项
document.getElementById("trNode").style.display = "none";

if('${fld:namespace@js}'!=''){
	var clause = "";
	var aa = 0;
	<rows>
		aa ++;
		clause += '<input type="checkbox" name="selectvalue" value="${fld:domain_value@js}" label="${fld:domain_text@js}" onclick="addStringName()">${fld:domain_text@js}&nbsp;';
		if(aa%3 == 0){
			clause += '<br/>';
		}
	</rows>
	
	document.getElementById("clause-span").innerHTML=clause;
	document.formEditor.phrase_name.readOnly = true;

	var s = "${fld:clause_value@js}".split(",");
	for(var b=0;b<s.length;b++){
		setCheckboxValue("selectvalue",s[b],"formEditor");
	}
}
