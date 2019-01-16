clearForm("formEditor");
document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.node_name.value = "${fld:node_name@js}";
document.formEditor.success_quota.value = "${fld:success_quota}";
document.formEditor.job_id.value = "${fld:job_id}";
document.formEditor.paper_id.value = "${fld:paper_id}";
document.formEditor.paper_name.value = "${fld:paper_name@js}";

document.formEditor.sms_template_id.value = "${fld:sms_template_id}";
document.formEditor.sms_template_name.value = "${fld:sms_template_name@js}";
document.formEditor.mms_template_id.value = "${fld:mms_template_id}";
document.formEditor.mms_template_name.value = "${fld:mms_template_name@js}";
document.formEditor.email_template_id.value = "${fld:email_template_id}";
document.formEditor.email_template_name.value = "${fld:email_template_name@js}";
document.formEditor.email_subject.value = "${fld:email_subject@js}";
document.formEditor.remind_template_id.value = "${fld:remind_template_id}";
document.formEditor.remind_template_name.value = "${fld:remind_template_name@js}";
document.formEditor.remind_subject.value = "${fld:remind_subject@js}";
document.formEditor.dm_job_id.value = "${fld:dm_job_id}";
document.formEditor.dm_job_name.value = "${fld:dm_job_name@js}";

document.formEditor.node_width.value = "${fld:node_width@js}";
document.formEditor.node_height.value = "${fld:node_height@js}";
document.formEditor.node_x.value = "${fld:node_x@js}";
document.formEditor.node_y.value = "${fld:node_y@js}";

document.formEditor.wait_time.value = "${fld:wait_time}";
if("${fld:wait_time}" != ""){
	var w = parseInt("${fld:wait_time}");
	if(w > (23*60 + 59)){//day
		var hm = w%(24*60);
		var d = (w-hm)/(24*60);
		document.formEditor.day_v.value = d;
		w = hm;
	}
	if(w > 59){//hour
		var m = w%60;
		var h = (w-m)/60;
		ccms.util.setComboValue("hour_v",h,"formEditor");
		w = m;
	}
	ccms.util.setComboValue("min_v",w,"formEditor");
}

ccms.util.setCheckboxValue("node_type","${fld:node_type@js}","formEditor");
ccms.util.setCheckboxValue("ob_type","${fld:ob_type@js}","formEditor");

ccms.util.setCheckboxValue("is_auto_assign","${fld:is_auto_assign@js}","formEditor");
document.formEditor.limit_dial_count.value = "${fld:limit_dial_count}";

ccms.util.setCheckboxValue("grab_flag","${fld:grab_flag@js}","formEditor");
ccms.util.setCheckboxValue("grab_skip_flag","${fld:grab_skip_flag@js}","formEditor");
ccms.util.setCheckboxValue("grab_flag_scope","${fld:grab_flag_scope}","formEditor");
ccms.util.setComboValue("email_send_type","${fld:email_send_type}","formEditor");

document.formEditor.remark.value = "${fld:remark@js}";

//获取当前节点真实坐标，并赋值
if(typeof(nodeObj) != "undefined"){
	document.formEditor.node_width.value = nodeObj.width;
	document.formEditor.node_height.value = nodeObj.height;
	document.formEditor.node_x.value = nodeObj.left;
	document.formEditor.node_y.value = nodeObj.top;
}