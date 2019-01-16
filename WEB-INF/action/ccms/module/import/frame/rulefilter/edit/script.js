ccms.util.clearForm("formEditor");

document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.clause_code.value = "${fld:clause_code}";
document.formEditor.field_type.value = "${fld:field_type}";
document.formEditor.clause_code_name.value = "${fld:field_name}";
document.formEditor.clause_filter.value = "${fld:clause_filter}";
document.formEditor.phrase_name.value = "${fld:phrase_name@js}";
document.formEditor.clause_value.value = "${fld:clause_value@js}";
document.formEditor.namespace.value = "${fld:namespace}";
document.formEditor.phrase_name1.value = "${fld:phrase_name1@js}";
document.formEditor.field_id.value = "${fld:field_id}";

document.formEditor.rule_filter_type.value = "${fld:rule_filter_type}";

ccms.util.setCheckboxValue("is_node","${fld:is_node}","formEditor");
ccms.util.setCheckboxValue("logic_type","${fld:logic_type}","formEditor");

changeRule("${fld:is_node}");

//隐藏选择节点项
$("#trNode").hide();

if('${fld:namespace}'!=''){
	var clause = "";
	var aa = 0;
	<rows>
		aa ++;
		clause += '<input type="checkbox" name="selectvalue" value="${fld:domain_value@js}" label="${fld:domain_text@js}" onclick="addStringName()">${fld:domain_text@js}&nbsp;';
		if(aa%3 == 0){
			clause += '<br/>';
		}
	</rows>
	
	$("#clause-span").html(clause);
	document.formEditor.phrase_name.readOnly = true;

	var s = "${fld:clause_value@js}".split(",");
	for(var b=0;b<s.length;b++){
		ccms.util.setCheckboxValue("selectvalue",s[b],"formEditor");
	}
}


var f = '${fld:clause_filter}';
if (f=="is not null" || f=="is null") {
	checkClause(f);
}

