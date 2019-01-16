SELECT 
	t.tuid AS target_group_id,
	t.target_year,
	t.target_month,
	t.target_type,
	t.pk_value,
	s.tuid AS target_staff_id,
	s.guest_target,
	s.follow_target,
	s.prepare_target,
	s.visit_target,
	s.ordernum_target,
	s.orderfee_target,
	s.call_target,
	s.call_mc_target,
	s.call_pt_target,
	s.test_target,
	s.unpayclass_target,
	s.allclass_target,
	s.site_target,
	s.user_id,
	s.userlogin,
	t.org_id 
FROM cc_target_group t 
INNER JOIN cc_target_staff s ON t.tuid = s.targetgroupid AND t.org_id = s.org_id 
WHERE t.target_type = 0 
AND t.target_year = ${target_year}::integer AND t.target_month = ${target_month}::integer 
AND t.org_id = ${fld:org_id}
