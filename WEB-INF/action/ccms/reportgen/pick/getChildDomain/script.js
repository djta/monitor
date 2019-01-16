
var obj = document.getElementById("${fld:select_id}");
if(obj){
	obj.options.length = 0;
	var option = new Option("","");
	obj.options.add(option);
	<rows>
		option = new Option("${fld:domain_text@js}","${fld:domain_value@js}");
		obj.options.add(option);
	</rows>
}