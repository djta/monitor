
var html = "";
<rows>
	html+="<tr id='${fld:user_id}'>"
			+"<td class='text-center' nowrap>${fld:name}"
			+"	<input type='hidden' id='tuid${fld:user_id}' name='user_id' value='${fld:user_id}' />"
			+"	<input type='hidden' id='userlogin${fld:user_id}' name='userlogin' value='${fld:userlogin}' />"
			+"</td>"
			+"<td id='tdguest_target' class='text-center' nowrap><span id='guest_target${fld:user_id}span' class='spantext'></span><input type='text' class='inputtext' id='guest_target${fld:user_id}' name='guest_target'/></td>"
			+"<td id='tdfollow_target' class='text-center' nowrap><span id='follow_target${fld:user_id}span' class='spantext'></span><input type='text' class='inputtext' id='follow_target${fld:user_id}' name='follow_target'/></td>"
			+"<td id='tdprepare_target' class='text-center' nowrap><span id='prepare_target${fld:user_id}span' class='spantext'></span><input type='text' class='inputtext' id='prepare_target${fld:user_id}' name='prepare_target'/></td>"
			+"<td id='tdvisit_target' class='text-center' nowrap><span id='visit_target${fld:user_id}span' class='spantext'></span><input type='text' class='inputtext' id='visit_target${fld:user_id}' name='visit_target'/></td>"
			+"<td id='tdordernum_target' class='text-center' nowrap><span id='ordernum_target${fld:user_id}span' class='spantext'></span><input type='text' class='inputtext' id='ordernum_target${fld:user_id}' name='ordernum_target'/></td>"
			+"<td id='tdorderfee_target' class='text-center' nowrap><span id='orderfee_target${fld:user_id}span' class='spantext'></span><input type='text' class='inputtext' id='orderfee_target${fld:user_id}' name='orderfee_target'/></td>"
			+"<td id='tdcall_target' class='text-center' nowrap><span id='call_target${fld:user_id}span' class='spantext'></span><input type='text' class='inputtext' id='call_target${fld:user_id}' name='call_target'/></td>"
			+"<td id='tdcall_mc_target' class='text-center' nowrap><span id='call_mc_target${fld:user_id}span' class='spantext'></span><input type='text' class='inputtext' id='call_mc_target${fld:user_id}' name='call_mc_target'/></td>"
			+"<td id='tdcall_pt_target' class='text-center' nowrap><span id='call_pt_target${fld:user_id}span' class='spantext'></span><input type='text' class='inputtext' id='call_pt_target${fld:user_id}' name='call_pt_target'/></td>"
			+"<td id='tdtest_target' class='text-center' nowrap><span id='test_target${fld:user_id}span' class='spantext'></span><input type='text' class='inputtext' id='test_target${fld:user_id}' name='test_target'/></td>"
			+"<td id='tdunpayclass_target' class='text-center' nowrap><span id='unpayclass_target${fld:user_id}span' class='spantext'></span><input type='text' class='inputtext' id='unpayclass_target${fld:user_id}' name='unpayclass_target'/></td>"
			+"<td id='tdallclass_target' class='text-center' nowrap><span id='allclass_target${fld:user_id}span' class='spantext'></span><input type='text' class='inputtext' id='allclass_target${fld:user_id}' name='allclass_target'/></td>"
			+"<td id='tdsite_target' class='text-center' nowrap><span id='site_target${fld:user_id}span' class='spantext'></span><input type='text' class='inputtext' id='site_target${fld:user_id}' name='site_target'/></td>"
			+"</tr>";
</rows>
$("#datagridstaffTemplate").html(html);
$(".inputtext").hide();
$(".spantext").hide();