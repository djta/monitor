document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.bannername.value = "${fld:bannername@js}";
document.formEditor.linkurl.value = "${fld:linkurl@js}";
document.formEditor.upload_id.value = "${fld:attachid}";
document.formEditor.showorder.value = "${fld:showorder}";
$("#uploadDiv").empty();
if( "" != "${fld:attachid}" ){
	$("#uploadDiv").append('<img width="375" height="160" src="${def:context}/action/ccms/attachment/download?id=${fld:attachid}&type=1&'+Math.random()+'" />');
}
