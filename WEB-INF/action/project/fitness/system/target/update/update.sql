UPDATE cc_target_group 
SET 
	target_year = ${fld:target_year},
	target_month = ${fld:target_month},
	pk_value = ${fld:pk_value},
	guest_target = ${fld:guest_target},
	follow_target = ${fld:follow_target},
	prepare_target = ${fld:prepare_target},
	visit_target = ${fld:visit_target},
	ordernum_target = ${fld:ordernum_target},
	orderfee_target = ${fld:orderfee_target},
	call_target = ${fld:call_target},
	call_mc_target = ${fld:call_mc_target},
	call_pt_target = ${fld:call_pt_target},
	test_target = ${fld:test_target},
	unpayclass_target = ${fld:unpayclass_target},
	allclass_target = ${fld:allclass_target},
	site_target = ${fld:site_target},
	remark = ${fld:remark},
	updatedby = '${def:user}',
	updated = {ts '${def:timestamp}'}
WHERE tuid = ${fld:vc_code} and org_id = ${def:org}

