<rows>
	if (typeof(document.forms["formEditor_${fld:form_id}"].elements["${fld:ctrl_code}_${fld:form_item_id}"]) != "undefined") {
		setCheckboxValue("${fld:ctrl_code}_${fld:form_item_id}","${fld:field_mark}","formEditor_${fld:form_id}");
	}
</rows>