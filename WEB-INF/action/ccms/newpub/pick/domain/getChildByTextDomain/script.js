
var index_domain = 1;
var first_value = "";

var obj = document.getElementById("${fld:text_id}");
if(obj){
	<rows>
		if(index_domain == 1){
			first_value = "${fld:domain_value}";
		}
		index_domain ++;
	</rows>
	obj.value = first_value;
}	