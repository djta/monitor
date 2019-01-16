
var index_domain = 1;
var first_value = "";
var province_value = "";
var area_value = "";

var obj = document.getElementById("city");
if(obj){
	obj.options.length = 0;
	var option = new Option("","");
	obj.options.add(option);
	<rows>
		if(index_domain == 1){
			first_value = "${fld:domain_value}";
			province_value = "${fld:parent_domain_value}";
			area_value = "${fld:parent_domain_value2}";
		}
		option = new Option("${fld:domain_text}","${fld:domain_value}");
		obj.options.add(option);
		index_domain ++;
	</rows>
	if(index_domain > 1){
		obj.options[1].selected = true;
	}
	//邮编
	if("${fld:area}"!=""){
		getChildByTextDomain("CityZip",first_value,"zip_code");
		setComboValue("province",province_value,"formEditor");
		setComboValue("area",area_value,"formEditor");
	}else{
		document.getElementById("city").selectedIndex=0;
		document.getElementById("province").selectedIndex=0;
	}
}	