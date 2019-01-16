

document.formEditor.vc_code.value="${fld:tuid}";
setSelectValue($("#pk_value"), "${fld:pk_value}");
document.formEditor.yearmonth.value="${fld:targetdate}";
document.formEditor.remark.value="${fld:remark@js}";
var code = $("#pk_value").find("option:selected").attr("code");

$("#zutype").html($("#pk_value").find("option:selected").html());
//0客服 1私教 2会籍
if(code=="0"){
	hidetarget();
	document.formEditor.call_target.value="${fld:call_target}";
	document.formEditor.call_pt_target.value="${fld:call_pt_target}";
	document.formEditor.call_mc_target.value="${fld:call_mc_target}";
	//客服
	$("#call_target,#call_mc_target,#call_pt_target")
	.parent().show();
}else if(code=="1"){
	hidetarget();
	document.formEditor.follow_target.value="${fld:follow_target}";
	document.formEditor.ordernum_target.value="${fld:ordernum_target}";
	document.formEditor.orderfee_target.value="${fld:orderfee_target}";
	document.formEditor.test_target.value="${fld:test_target}";
	document.formEditor.unpayclass_target.value="${fld:unpayclass_target}";
	document.formEditor.allclass_target.value="${fld:allclass_target}";
	document.formEditor.site_target.value="${fld:site_target}";
	//私教
	$("#follow_target,#ordernum_target,#orderfee_target,#test_target,#unpayclass_target,#allclass_target,#site_target")
	.parent().show();
}else if(code=="2"){
	hidetarget();
	document.formEditor.guest_target.value="${fld:guest_target}";
	document.formEditor.follow_target.value="${fld:follow_target}";
	document.formEditor.prepare_target.value="${fld:prepare_target}";
	document.formEditor.visit_target.value="${fld:visit_target}";
	document.formEditor.ordernum_target.value="${fld:ordernum_target}";
	document.formEditor.orderfee_target.value="${fld:orderfee_target}";
	document.formEditor.call_target.value="${fld:call_target}";
	//会籍
	$("#guest_target,#follow_target,#prepare_target,#visit_target,#ordernum_target,#orderfee_target,#call_target")
	.parent().show();
}
var url="${def:context}${def:actionroot}/skillsearch?skill_scope="+code;
	ajaxCall(url,{
   	method: "get",
		progress: true,
		dataType: "script",
   	success: function() {
		}
	});