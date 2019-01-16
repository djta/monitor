ccms.dialog.notice("流程办理成功！",1500, function(){
	//如果下一节点是子流程节点，则直接跳转到子流程界面
	if(("${fld:next_step_type}" == "4" || "${fld:next_step_type}" == "3") && "${fld:child_wfm_id}" != ""){
		openChildWfm("${fld:pk_value}","${fld:document_id}","${fld:child_wfm_id}","${fld:wfentry_id}","${fld:next_node}","${fld:next_step_type}");
	}else{
		if($Mobile.isWeiXin()){
			//gotoPage("/action/ccms/wfmgen/ownerPhone/crud");
			window.location.reload();
		}else{
			//gotoPage("/action/ccms/wfmgen/owner/crud");
			window.location.reload();
		}
	}
});