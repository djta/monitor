
var h = '<span ><a href="javascript:void(0);"  onclick="selectAll(\'staff_id\',\'formEditor\');">全选</a>-<a href="javascript:void(0);" onclick="unselectAll(\'staff_id\',\'formEditor\');">反选</a></span><br>';
var i = 1;
<krows>
	h += '<input name="staff_id" type="checkbox" value="${fld:user_id}" >${fld:name}&nbsp';
    if(i%6==0) h += '<br>';
    i++;
</krows>

document.getElementById("staffTd").innerHTML = h;