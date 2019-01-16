var obj = document.getElementById("${fld:field_id}");
if(obj){
	var innerHTML = "";
	<rows>
		innerHTML = innerHTML+'<input type="checkbox" name="${fld:child_name}" value="${fld:domain_value@js}">${fld:domain_text@js}</input>';
	</rows>
	obj.innerHTML = innerHTML;
}

$(':input[type=checkbox]').iCheck({
	checkboxClass: 'icheckbox_square-blue',
	increaseArea: '20%'
	 }); 