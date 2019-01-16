
var index_domain = 1;
var first_value = "";

var obj = document.getElementById("${fld:select_id}");
if(obj){
	obj.options.length = 0;
	var option = new Option("","");
	obj.options.add(option);
	<rows>
		if(index_domain == 1){
			first_value = "${fld:domain_value}";
		}
		option = new Option("${fld:domain_text}","${fld:domain_value}");
		obj.options.add(option);
		index_domain ++;
	</rows>
	if(index_domain > 1){
		obj.options[1].selected = true;
	}
	var next_namespace = "${fld:next_namespace}";
	if(next_namespace != ""){
		getChildDomain("${fld:namespace}",first_value,next_namespace,"${fld:next_select_id}","","");
	}
}	