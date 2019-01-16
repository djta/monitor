if(document.formEditor.area){
	setComboValue("area","${fld:parent_domain_value2}","formEditor");
}
setComboValue("province","${fld:parent_domain_value}","formEditor");
getChildByTextDomain("CityZip","${fld:city}","zip_code");