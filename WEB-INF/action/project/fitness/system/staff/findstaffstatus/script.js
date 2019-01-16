
var status = "${fld:status}";
if(parseInt("${fld:num}")!=parseInt("${fld:len}")){
	ccms.dialog.alert("当前选择的员工存在"+(status==1?'离职':'在职')+"员工，不能一起操作！", 2000);
}else{
	var url = "${def:context}${def:actionroot}/updatestatus?user_id=${fld:user_id}&status="+status;
	ajaxCall(url,{
		method: "get",
		progress: true,
		dataType: "script",
		success: function() {
			ccms.dialog.notice((status==1?'复职':'离职')+"成功！", 2000,function(){
				search.searchData(1);
			});
		}
	});
}

