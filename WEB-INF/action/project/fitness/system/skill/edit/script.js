clearForm("formEditor");
document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.skill_name.value = "${fld:skill_name@js}";
document.formEditor.remark.value = "${fld:remark@js}";

treeObj.checkAllNodes(false);
var node;
<menu-list>
node = treeObj.getNodeByParam("id", "${fld:menu_id}", null);
if( null != node){
	treeObj.checkNode(node, true, null, null);
}
</menu-list>

<role-list>
setCheckboxValue("role_id","${fld:role_id}","formEditor");
</role-list>