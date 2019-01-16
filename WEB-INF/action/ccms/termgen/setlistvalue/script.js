var itemObj = null;
<rows>
if("${fld:item_show_type}" == "0"){
	itemObj=$("#${fld:item_code}_${fld:list_code}");
	if(itemObj.length > 0){
		itemObj.val("${fld:list_text@js}");
	}
}else{
	if("${fld:list_show_type}"=="3"){	/*下拉框*/
		itemObj = $("input[name='${fld:item_code}']")
		if(itemObj.length > 0){
			$("#${fld:item_code}_${fld:list_code}").iCheck("check");
			$("#${fld:item_code}_${fld:list_code}"+"_span").show();
			if("${fld:namespace_op}"=="1"){
				setMulitCheckboxValue("${fld:item_code}_${fld:list_code}_check","${fld:list_dropdown_value@js}","termForm");
			}else{
				setComboValue("${fld:item_code}_${fld:list_code}_select","${fld:list_dropdown_value@js}","termForm");
			}
			$("#${fld:item_code}_${fld:list_code}_select").val("${fld:list_dropdown_value@js}");
		}
	}
	else if("${fld:list_show_type}"=="0"){//标签
		itemObj = $("input[name='${fld:item_code}']")
		if(itemObj.length > 0){
			$("#${fld:item_code}_${fld:list_code}").iCheck("check");
			$("#${fld:item_code}_${fld:list_code}"+"_span").show();
		}
	}else if("${fld:list_show_type}"=="2"){//标签加文本
		itemObj = $("input[name='${fld:item_code}']")
		if(itemObj.length > 0){
			$("#${fld:item_code}_${fld:list_code}").iCheck("check");
			$("#${fld:item_code}_${fld:list_code}"+"_span").show();
			//“备注文本框”赋值
			$("#${fld:item_code}_${fld:list_code}_text").val("${fld:list_text@js}");
		}
	}else if("${fld:list_show_type}"=="1"){//文本框
		itemObj=$("#${fld:item_code}_${fld:list_code}");
		if(itemObj.length > 0){
			itemObj.val("${fld:list_text@js}");
		}
	}
}
</rows>