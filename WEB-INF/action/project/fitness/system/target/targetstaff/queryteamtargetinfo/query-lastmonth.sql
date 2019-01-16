select 
	tg.guest_target as last_guest_target,
	tg.follow_target as last_follow_target,
	tg.prepare_target as last_prepare_target,
	tg.visit_target as last_visit_target,
	tg.ordernum_target as last_ordernum_target,
	tg.orderfee_target as last_orderfee_target,
	tg.call_target as last_call_target,
	tg.call_mc_target as last_call_mc_target,
	tg.call_pt_target as last_call_pt_target,
	tg.test_target as last_test_target,
	tg.unpayclass_target as last_unpayclass_target,
	tg.allclass_target as last_allclass_target,
	tg.site_target as last_site_target
from cc_target_group tg
left join hr_team team ON tg.pk_value = team.team_id and tg.org_id = team.org_id
inner join (select target_type from cc_target_group where tuid = ${fld:tuid}::int and org_id = ${def:org}) ttype
	on ttype.target_type = tg.target_type
where tg.org_id=${def:org} and team.skill_scope = ${fld:skill_scope}
and tg.target_year||'-'||tg.target_month = 
	to_char((((select target_year||'-'||target_month from cc_target_group where tuid = ${fld:tuid}::int  and org_id = ${def:org})||'-01' )::date - ('1 month')::interval),'yyyy-MM')
