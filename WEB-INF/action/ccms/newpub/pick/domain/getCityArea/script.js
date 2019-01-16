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
		//obj.options[1].selected = true;
	}
	if("${fld:city_old}" != ""){//界面修改时需要清除多余的城市，清除时候需要让清除前的选中
		//邮编
		//getChildByTextDomain("CityZip","${fld:city_old}","zip_code");
		setComboValue("city","${fld:city_old}","formEditor");
	}else{
		//邮编
		//getChildByTextDomain("CityZip",first_value,"zip_code");
	}
	if("${fld:province}"!=""){
		setComboValue("province",province_value,"formEditor");
		if(document.formEditor.area){
			setComboValue("area",area_value,"formEditor");
		}
	}
}	