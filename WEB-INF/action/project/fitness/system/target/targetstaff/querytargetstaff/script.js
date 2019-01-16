var count = 0;
<rows>
$('#guest_target${fld:user_id}span').html("${fld:guest_target}");
$('#guest_target${fld:user_id}span').show();
$('#guest_target${fld:user_id}').val("${fld:guest_target}");

$('#follow_target${fld:user_id}span').html("${fld:follow_target}");
$('#follow_target${fld:user_id}span').show();
$('#follow_target${fld:user_id}').val("${fld:follow_target}");

$('#prepare_target${fld:user_id}span').html("${fld:prepare_target}");
$('#prepare_target${fld:user_id}span').show();
$('#prepare_target${fld:user_id}').val("${fld:prepare_target}");

$('#visit_target${fld:user_id}span').html("${fld:visit_target}");
$('#visit_target${fld:user_id}span').show();
$('#visit_target${fld:user_id}').val("${fld:visit_target}");

$('#ordernum_target${fld:user_id}span').html("${fld:ordernum_target}");
$('#ordernum_target${fld:user_id}span').show();
$('#ordernum_target${fld:user_id}').val("${fld:ordernum_target}");

$('#orderfee_target${fld:user_id}span').html("${fld:orderfee_target}");
$('#orderfee_target${fld:user_id}span').show();
$('#orderfee_target${fld:user_id}').val("${fld:orderfee_target}");

$('#call_target${fld:user_id}span').html("${fld:call_target}");
$('#call_target${fld:user_id}span').show();
$('#call_target${fld:user_id}').val("${fld:call_target}");

$('#call_mc_target${fld:user_id}span').html("${fld:call_mc_target}");
$('#call_mc_target${fld:user_id}span').show();
$('#call_mc_target${fld:user_id}').val("${fld:call_mc_target}");

$('#call_pt_target${fld:user_id}span').html("${fld:call_pt_target}");
$('#call_pt_target${fld:user_id}span').show();
$('#call_pt_target${fld:user_id}').val("${fld:call_pt_target}");

$('#test_target${fld:user_id}span').html("${fld:test_target}");
$('#test_target${fld:user_id}span').show();
$('#test_target${fld:user_id}').val("${fld:test_target}");

$('#unpayclass_target${fld:user_id}span').html("${fld:unpayclass_target}");
$('#unpayclass_target${fld:user_id}span').show();
$('#unpayclass_target${fld:user_id}').val("${fld:unpayclass_target}");

$('#allclass_target${fld:user_id}span').html("${fld:allclass_target}");
$('#allclass_target${fld:user_id}span').show();
$('#allclass_target${fld:user_id}').val("${fld:allclass_target}");

$('#site_target${fld:user_id}span').html("${fld:site_target}");
$('#site_target${fld:user_id}span').show();
$('#site_target${fld:user_id}').val("${fld:site_target}");
count++;
</rows>
if(count==0){
	var len =  $("#datagridstaffTemplate tr").length;
	if(len>0){
		var skill_scope = "${fld:skill_scope}";
		
		var call_target = "${fld:call_target}";
		var call_target_computations = computations(call_target,len);
		var call_target_remainder = remainder(call_target,len);
		
		var call_mc_target = "${fld:call_mc_target}";
		var call_mc_target_computations = computations(call_mc_target,len);
		var call_mc_target_remainder = remainder(call_mc_target,len);
		
		var call_pt_target = "${fld:call_pt_target}";
		var call_pt_target_computations = computations(call_pt_target,len);
		var call_pt_target_remainder = remainder(call_pt_target,len);
		
		var follow_target = "${fld:follow_target}";
		var follow_target_computations = computations(follow_target,len);
		var follow_target_remainder = remainder(follow_target,len);

		var ordernum_target = "${fld:ordernum_target}";
		var ordernum_target_computations = computations(ordernum_target,len);
		var ordernum_target_remainder = remainder(ordernum_target,len);

		var test_target = "${fld:test_target}";
		var test_target_computations = computations(test_target,len);
		var test_target_remainder = remainder(test_target,len);
		
		var unpayclass_target = "${fld:unpayclass_target}";
		var unpayclass_target_computations = computations(unpayclass_target,len);
		var unpayclass_target_remainder = remainder(unpayclass_target,len);
		
		var allclass_target = "${fld:allclass_target}";
		var allclass_target_computations = computations(allclass_target,len);
		var allclass_target_remainder = remainder(allclass_target,len);
		
		var site_target = "${fld:site_target}";
		var site_target_computations = computations(site_target,len);
		var site_target_remainder = remainder(site_target,len);
		
		var guest_target = "${fld:guest_target}";
		var guest_target_computations = computations(guest_target,len);
		var guest_target_remainder = remainder(guest_target,len);
		
		var prepare_target = "${fld:prepare_target}";
		var prepare_target_computations = computations(prepare_target,len);
		var prepare_target_remainder = remainder(prepare_target,len);

		var visit_target = "${fld:visit_target}";
		var visit_target_computations = computations(visit_target,len);
		var visit_target_remainder = remainder(visit_target,len);
		if(skill_scope=="0"){
			//客服
			cycle("call_target", call_target_computations, call_target_remainder);
			cycle("call_mc_target", call_mc_target_computations, call_mc_target_remainder);
			cycle("call_pt_target", call_pt_target_computations, call_pt_target_remainder);
		}else if(skill_scope=="1"){
			//私教
			cycle("follow_target", follow_target_computations, follow_target_remainder);
			cycle("ordernum_target", ordernum_target_computations, ordernum_target_remainder);
			cycle("test_target", test_target_computations, test_target_remainder);
			cycle("unpayclass_target", unpayclass_target_computations, unpayclass_target_remainder);
			cycle("allclass_target", allclass_target_computations, allclass_target_remainder);
			cycle("site_target", site_target_computations, site_target_remainder);
			var ot1= 0 ;
			var ot2= 0 ;
			var orderfee_target = "${fld:orderfee_target}";
			if(orderfee_target==""){
				orderfee_target=0;
			}
			orderfee_target = Number(orderfee_target);
			ot1 =Number(orderfee_target/len).toFixed(2);
			ot2 =Number(orderfee_target%len).toFixed(2);
			cycle("orderfee_target", ot1, ot2);
		}else if(skill_scope=="2"){
			//会籍
			cycle("guest_target", guest_target_computations, guest_target_remainder);
			cycle("follow_target", follow_target_computations, follow_target_remainder);
			cycle("prepare_target", prepare_target_computations, prepare_target_remainder);
			cycle("ordernum_target", ordernum_target_computations, ordernum_target_remainder);
			cycle("visit_target", visit_target_computations, visit_target_remainder);
			cycle("call_target", call_target_computations, call_target_remainder);
			
			var ot1= 0 ;
			var ot2= 0 ;
			var orderfee_target = "${fld:orderfee_target}";
			if(orderfee_target==""){
				orderfee_target=0;
			}
			orderfee_target = Number(orderfee_target);
			ot1 =Number(orderfee_target/len).toFixed(2);
			ot2 =Number(orderfee_target%len).toFixed(2);
			cycle("orderfee_target", ot1, ot2);
		}
	}
}

function computations(val,len){
	if(val==""){
		val=0;
	}
	val =val-remainder(val,len);
	return parseInt(val)/parseInt(len);
}
function remainder(val,len){
	if(val==""){
		val=0;
	}
	return parseInt(val)%parseInt(len);
}

function cycle(val, computations, remainder){
	var num = 0;
	$("#datagridstaffTemplate tr").each(function(){
		var tuid = $(this).attr("id");
		if(num==0){
			$('#'+val+tuid).val(Number(computations)+Number(remainder));
			$('#'+val+tuid+"span").html(Number(computations)+Number(remainder));
		}else{
			$('#'+val+tuid).val(computations);
			$('#'+val+tuid+"span").html(computations);
		}
		$('#'+val+tuid+"span").show();
		num++;
	});
}
