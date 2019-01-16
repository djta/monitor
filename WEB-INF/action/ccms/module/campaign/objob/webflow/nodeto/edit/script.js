var b = document.getElementById("gridBody");

var j = 0;
<rows>
	j++;

	addRow();
	
	b = document.getElementById("gridBody");
	setSelectValue(document.forms[0].rule_field[b.rows.length-1],"${fld:rule_field}");
	setSelectValue(document.forms[0].rule_operator[b.rows.length-1],"${fld:rule_operator@js}");
	document.forms[0].rule_value[b.rows.length-1].value = "${fld:rule_value@js}";
	document.forms[0].left_prefix[b.rows.length-1].value = "${fld:left_prefix@js}";
	document.forms[0].right_suffix[b.rows.length-1].value = "${fld:right_suffix@js}";
	setSelectValue(document.forms[0].rule_logic[b.rows.length-1],"${fld:rule_logic@js}");

</rows>

if(j > 0){//如果修改时有数据则需要删除保留的一条
	b.deleteRow(0);
}

document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.node_id.value = "${fld:node_id}";
document.formEditor.next_node.value = "${fld:next_node}";

