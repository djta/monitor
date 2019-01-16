var obj = document.getElementById("areaListSpan");
if(obj){
	var innerHTML = "";
	<rows>
		innerHTML = innerHTML+'<input type="checkbox" name="area" value="${fld:area_code}">${fld:area_name}</input>';
	</rows>
	obj.innerHTML = innerHTML;
}
$(':input[type=checkbox]').iCheck({
	checkboxClass: 'icheckbox_square-blue',
	increaseArea: '20%'
	 }); 