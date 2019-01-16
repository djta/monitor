<rows>
if (typeof(document.forms["formEditor_${fld:form_id}"].elements["${fld:ctrl_code}_${fld:form_item_id}"]) != "undefined" && "${fld:show_type}"!="1" && "${fld:show_type}"!="2" && "${fld:show_type}"!="3") {/*1下拉框2多选3单选*/
    document.forms["formEditor_${fld:form_id}"].elements["${fld:ctrl_code}_${fld:form_item_id}"].value = "${fld:field_mark}";
}
</rows>