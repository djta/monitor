
//从数据库中获取一个序列号
function getSequence(id,backFunc){
	var url="/action/ccms/module/workflow/webflow/sequence" + "?id=" + id;
	ajaxCall(url,{
		method: "POST",
		progress: true,
		dataType: "script",
		success:function(){
			backFunc;
		}
	});
}