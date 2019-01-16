select
	tuid,
	pk_value,
	(target_year||'-'||target_month) as targetdate,
	guest_target,
	follow_target,
	prepare_target,
	visit_target,
	ordernum_target,
	orderfee_target,
	call_target,
	call_mc_target,
	call_pt_target,
	test_target,
	unpayclass_target,
	allclass_target,
	site_target,
	remark
from cc_target_group
where tuid = ${fld:id} and org_id = ${def:org}