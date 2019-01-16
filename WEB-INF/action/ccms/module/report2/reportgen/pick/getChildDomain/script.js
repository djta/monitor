
var obj = document.getElementById("${fld:select_id}");
if(obj){
	obj.options.length = 0;
	var option = new Option("","");
	obj.options.add(option);
	<rows>
		option = new Option("${fld:domain_text}","${fld:domain_value}");
		obj.options.add(option);
	</rows>
}