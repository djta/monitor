
var guest_target = 0;
var prepare_target = 0;
var visit_target = 0;
var ordernum_target = 0;
var orderfee_target = 0;
var follow_target = 0;
var call_target = 0;

<group-rows>
guest_target = parseInt("${fld:guest_target}");
prepare_target = parseInt("${fld:prepare_target}");
visit_target = parseInt("${fld:visit_target}");
ordernum_target = parseInt("${fld:ordernum_target}");
orderfee_target = Number("${fld:orderfee_target}");
follow_target = parseInt("${fld:follow_target}");
call_target = parseInt("${fld:call_target}");call_pt_target
call_mc_target = parseInt("${fld:call_mc_target}");
call_pt_target = parseInt("${fld:call_pt_target}");
allclass_target = parseInt("${fld:allclass_target}");
unpayclass_target = parseInt("${fld:unpayclass_target}");
test_target = parseInt("${fld:test_target}");
site_target = parseInt("${fld:site_target}");
</group-rows>
$("#zytype,#yuedetail").html("");
var html = "";
var count = 0;
<rows>
	html+="${fld:name} ";
	count++;
</rows>
$("#zytype").html(html);
if(count==0){
	$("#zytype").html("上月没记录！");
}else{

	var ziyuan = parseInt("${fld:ziyuan}");//资源获取量
	var yuyuenum = parseInt("${fld:yuyuenum}");//预约量
	var laifangliang = parseInt("${fld:laifangliang}");//实际到访量
	var chengdanliang = parseInt("${fld:chengdanliang}");//成单量
	var genjinnum = parseInt("${fld:genjinnum}");//跟进量
	var normalmoney = "${fld:normalmoney}";//成单额
	if(normalmoney=="")normalmoney = 0;
	var huifang = parseInt("${fld:huifang}");//回访量
	var huifanghuiji = parseInt("${fld:huifanghuiji}");//回访预约会籍量
	var huifangsijiao = parseInt("${fld:huifangsijiao}");//回访预约私教量

	var zongshangke = parseInt("${fld:zongshangke}");//总上课量
	var tiyanshangke = parseInt("${fld:tiyanshangke}");//体验课上课量
	var tice = parseInt("${fld:tice}");//体测量
	var changkai = parseInt("${fld:changkai}");//场开量
	
	var targethtml = "";
	//0客服 1私教 2会籍
	if("${fld:skill_scope}"=="0"){
		//客服
		/*$("#call_target,#call_mc_target,#call_pt_target")
		.parent().parent().show();*/
		targethtml += targetval(call_target, huifang, "回访量：");
		targethtml += targetval(call_mc_target, huifanghuiji, "回访预约会籍量：");
		targethtml += targetval(call_pt_target, huifangsijiao, "回访预约私教量：");
	}else if("${fld:skill_scope}"=="1"){
		//私教
		/*$("#follow_target,#ordernum_target,#orderfee_target,#test_target,#unpayclass_target,#allclass_target,#site_target")
		.parent().parent().show();*/
		targethtml += targetval(follow_target, genjinnum, "跟进量：");
		targethtml += targetval(ordernum_target, chengdanliang, "成单量：");
		targethtml += targetval(orderfee_target, normalmoney, "成单额：");
		targethtml += targetval(test_target, tice, "体测量：");
		targethtml += targetval(unpayclass_target, tiyanshangke, "体验课上课量：");
		targethtml += targetval(allclass_target, zongshangke, "总上课量：");
		targethtml += targetval(site_target, changkai, "场开量：");
	}else if("${fld:skill_scope}"=="2"){
		//会籍
		/*$("#guest_target,#follow_target,#prepare_target,#visit_target,#ordernum_target,#orderfee_target")
		.parent().parent().show();*/
		targethtml += targetval(guest_target, ziyuan, "资源获取量：");
		targethtml += targetval(follow_target, genjinnum, "跟进量：");
		targethtml += targetval(prepare_target, yuyuenum, "预约量：");
		targethtml += targetval(visit_target, laifangliang, "实际到访量：");
		targethtml += targetval(ordernum_target, chengdanliang, "成单量：");
		targethtml += targetval(orderfee_target, normalmoney, "成单额：");
	}
	$("#yuedetail").html(targethtml);
}

function targetval(target, val, text){
	var num = Math.floor(((target-(target-val))*100/target)*100)/100;
	if(isNaN(num))num=0;
	return text+val+", 完成"+num+"%&nbsp;&nbsp;";
}
