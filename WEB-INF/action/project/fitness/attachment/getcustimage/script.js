var obj = $("#${fld:objid}");
obj.empty();
var picIdx = 0;
<pic-rows>
picIdx++;
obj.append('<img style="width:210px;height:310px;" src="${def:context}/action/ccms/attachment/download?id=${fld:upload_id}&&type=1">');
</pic-rows>
if( picIdx <= 0 ){
	// obj.append('(无照片)');
	obj.append('<img style="width:210px;height:310px;" src="${def:context}/icon_head.jpg">');
}


// obj.append('<img style="width:210px;height:310px;" src="${def:context}/action/ccms/attachment/download?id=${fld:upload_id}&&type=1">');
