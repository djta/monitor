document.formEditor.team_id.value = "${fld:team_id}";
document.formEditor.team_name.value = "${fld:team_name@js}";

var member_tuid="${fld:member_tuid}".split(',');
for(var i=0;i<member_tuid.length;i++){
		$("input[name=emplist][value="+member_tuid[i]+"]").iCheck('check');      
}

var index="${fld:skill_scope}"
if(index==0){
	$("input[name=skill_scope]:eq(2)").iCheck('check');
}else if(index==1){
	$("input[name=skill_scope]:eq(1)").iCheck('check');
}else{
	$("input[name=skill_scope]:eq(0)").iCheck('check');
}
$("input[name=data_limit][value=${fld:data_limit}]").iCheck('check');      

setSelectValue($("#leader_id"), "${fld:leader_id}");
