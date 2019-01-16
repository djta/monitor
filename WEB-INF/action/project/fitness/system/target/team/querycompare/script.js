
var count = 0; 
var id = "${fld:id}";
<rows>
	targetval("${fld:num}", "${fld:targetval}", id);
	count++;
</rows>
if(count==0){
	$("#"+id).html("上月没有记录！");
}
//计算
function targetval(val, last_val, id){
	if(last_val!=""){
		//当前月数量
		var val_num = Number(val);
		//上月数量
		var last_val_num = Number(last_val);
		if(val_num==0&&last_val_num==0){
			$("#"+id).html("比上月提升+0%");
		}else if(val_num==0){
			$("#"+id).html("比上月提升-0%");
		}else if(last_val_num==0){
			$("#"+id).html("比上月提升+0%");
		}else{
			var target = val_num-last_val_num;
			if(target>=0){
				var val = (target*100)/last_val_num;
				$("#"+id).html("<article   class='r-tab-cout-1-r-t dialogbg'><li><span><i></i>"+val.toFixed(2)+"%</span></article>");
			}else{
				var val = (target*100)/last_val_num;
				$("#"+id).html("<article   class='r-tab-cout-1-r-t dialogbg'><li ><span class='down'><i ></i>"+val.toFixed(2)+"%</span></article>");
			}
		}
	}else{
		$("#"+id).html("上月没有记录！");
	}
}
