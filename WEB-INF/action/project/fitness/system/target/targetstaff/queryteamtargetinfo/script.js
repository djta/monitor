
var html = "";
html+="组名：${fld:team_name}&nbsp;&nbsp;组类别：${fld:skill_scope_name}&nbsp;&nbsp;组长：${fld:staff_name}&nbsp;&nbsp;共${fld:staffnum}人<div><nav>";

var skill_scope = "${fld:skill_scope}";
if(skill_scope=="0"){
	//客服
	html+="<li><p><span>回访量：${fld:call_target}</span><span id='call_target${fld:tuid}'></span></p>";
	html+="<p><span>回访预约会籍量：${fld:call_mc_target}</span><span id='call_mc_target${fld:tuid}'></span></p>";
	html+="<p><span>回访预约私教量：${fld:call_pt_target}</span><span id='call_pt_target${fld:tuid}'></span></p></li>";
}else if(skill_scope=="1"){
	//私教
	html+="<li><p><span>跟进量：${fld:follow_target}</span><span id='follow_target${fld:tuid}'></span></p>";
	html+="<p><span>成单量：${fld:ordernum_target}</span><span id='ordernum_target${fld:tuid}'></span></p>";
	html+="<p><span>成单额：${fld:orderfee_target}</span><span id='orderfee_target${fld:tuid}'></span></p></li>";
	html+="<li><p><span>体测量：${fld:test_target}</span><span id='test_target${fld:tuid}'></span></p>";
	html+="<p><span>体验课上课量：${fld:unpayclass_target}</span><span id='unpayclass_target${fld:tuid}'></span></p>";
	html+="<p><span>总上课量：${fld:allclass_target}</span><span id='allclass_target${fld:tuid}'></span></p></li>";
	html+="<li><p><span>场开量：${fld:site_target}</span><span id='site_target${fld:tuid}'></span></p></li>";
}else if(skill_scope=="2"){
	//会籍
	html+="<li><p><span>资源获取量：${fld:guest_target}</span><span id='guest_target${fld:tuid}'></span><p>";
	html+="<p><span>跟进量：${fld:follow_target}</span><span id='follow_target${fld:tuid}'></span><p>";
	html+="<p><span>预约量：${fld:prepare_target}</span><span id='prepare_target${fld:tuid}'></span></p></li>";
	html+="<li><p><span>实际到访量：${fld:visit_target}</span><span id='visit_target${fld:tuid}'></span><p>";
	html+="<p><span>成单量：${fld:ordernum_target}</span><span id='ordernum_target${fld:tuid}'></span><p>";
	html+="<p><span>成单额：${fld:orderfee_target}</span><span id='orderfee_target${fld:tuid}'></span></p></li>";
	html+="<li><p><span>回访量：${fld:call_target}</span><span id='call_target${fld:tuid}'></span></p></li>";
}
$("#targetinfo").html(html+"</nav></div>");

var count = 0;
var guest_target = "${fld:guest_target}";
var follow_target = "${fld:follow_target}";
var prepare_target = "${fld:prepare_target}";
var visit_target = "${fld:visit_target}";
var ordernum_target = "${fld:ordernum_target}";
var orderfee_target = "${fld:orderfee_target}";
var call_target = "${fld:call_target}";
var call_mc_target = "${fld:call_mc_target}";
var call_pt_target = "${fld:call_pt_target}";
var test_target = "${fld:test_target}";
var unpayclass_target = "${fld:unpayclass_target}";
var allclass_target = "${fld:allclass_target}";
var site_target = "${fld:site_target}";
<rows>
	//资源获取量
	targetval(guest_target, "${fld:last_guest_target}", "guest_target");
	//跟进量
	targetval(follow_target, "${fld:last_follow_target}", "follow_target");
	//预约量
	targetval(prepare_target, "${fld:last_prepare_target}", "prepare_target");
	//实际到访量
	targetval(visit_target, "${fld:last_visit_target}", "visit_target");
	//成单量
	targetval(ordernum_target, "${fld:last_ordernum_target}", "ordernum_target");
	//成单额
	targetval(orderfee_target, "${fld:last_orderfee_target}", "orderfee_target");
	//回访量
	targetval(call_target, "${fld:last_call_target}", "call_target");
	//回访预约会籍量
	targetval(call_mc_target, "${fld:last_call_mc_target}", "call_mc_target");
	//回访预约私教量
	targetval(call_pt_target, "${fld:last_call_pt_target}", "call_pt_target");
	//体测量
	targetval(test_target, "${fld:last_test_target}", "test_target");
	//体验课上课量
	targetval(unpayclass_target, "${fld:last_unpayclass_target}", "unpayclass_target");
	//总上课量
	targetval(allclass_target, "${fld:last_allclass_target}", "allclass_target");
	//场开量
	targetval(site_target, "${fld:last_site_target}", "site_target");
	count++;
</rows>

//计算
function targetval(val, last_val, id){
	if(last_val!=""&&val!=""){
		//当前月数量
		var val_num = Number(val);
		//上月数量
		var last_val_num = Number(last_val);
		if(val_num==0&&last_val_num==0){
			$("#"+id+"${fld:tuid}").html("<i>环比+0%</i>");
		}else if(val_num==0){
			$("#"+id+"${fld:tuid}").html("<i>环比-0%</i>");
		}else if(last_val_num==0){
			$("#"+id+"${fld:tuid}").html("<i>环比+0%</i>");
		}else{
			var target = val_num-last_val_num;
			var val = (target*100)/last_val_num;
			if(val<0){
				$("#"+id+"${fld:tuid}").addClass("down");
			}
			$("#"+id+"${fld:tuid}").html("<i style='margin-left: -15px'>环比"+val.toFixed(2)+"%</i>");
		}
	}else{
		$("#"+id+"${fld:tuid}").html("<i>环比+0%</i>");
	}
}

if(count==0){
	$("#guest_target${fld:tuid}").html("<i>环比+0%</i>");
	$("#follow_target${fld:tuid}").html("<i>环比+0%</i>");
	$("#prepare_target${fld:tuid}").html("<i>环比+0%</i>");
	$("#visit_target${fld:tuid}").html("<i>环比+0%</i>");
	$("#ordernum_target${fld:tuid}").html("<i>环比+0%</i>");
	$("#orderfee_target${fld:tuid}").html("<i>环比+0%</i>");
	$("#call_target${fld:tuid}").html("<i>环比+0%</i>");
	$("#call_mc_target${fld:tuid}").html("<i>环比+0%</i>");
	$("#call_pt_target${fld:tuid}").html("<i>环比+0%</i>");
	$("#test_target${fld:tuid}").html("<i>环比+0%</i>");
	$("#unpayclass_target${fld:tuid}").html("<i>环比+0%</i>");
	$("#allclass_target${fld:tuid}").html("<i>环比+0%</i>");
	$("#site_target${fld:tuid}").html("<i>环比+0%</i>");
}