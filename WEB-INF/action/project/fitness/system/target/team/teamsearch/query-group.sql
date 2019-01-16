select 
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
	site_target
from cc_target_group 
inner join (select team_id from hr_team where skill_scope = ${fld:skill_scope}
and org_id = ${def:org}) team on team.team_id = pk_value
where target_type=0 and org_id = ${def:org} and
concat(target_year||'-'||target_month)=(select to_char(('${def:date}'::date- ('1 month')::interval), 'yyyy-MM'))
