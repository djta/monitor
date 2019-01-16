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
	
	document.getElementById("clause-span").innerHTML=clause;
	document.formEditor.phrase_name.value = "";
	document.formEditor.phrase_name.readOnly = true;
}else{
	document.formEditor.phrase_name.value = "";
	document.formEditor.phrase_name.readOnly = false;
}