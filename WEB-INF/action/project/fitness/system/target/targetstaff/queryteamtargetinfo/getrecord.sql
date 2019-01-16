select 
	tg.guest_target,
	tg.follow_target,
	tg.prepare_target,
	tg.visit_target,
	tg.ordernum_target,
	tg.orderfee_target,
	tg.call_target,
	tg.call_mc_target,
	tg.call_pt_target,
	tg.test_target,
	tg.unpayclass_target,
	tg.allclass_target,
	tg.site_target,
	team.team_name,
	(case when skill_scope='0' then '客服' when skill_scope='1' then '私教' when skill_scope='2' then '会籍' end) as skill_scope_name,
	tg.target_year||'-'||tg.target_month as yearmonth,
	(select count(1) from hr_staff 
		inner join (select user_id::int from hr_team_staff where team_id = tg.pk_value)  ts
		on ts.user_id = hr_staff.user_id
	where org_id = ${def:org}) as staffnum,
	(select name from hr_staff where user_id = team.leader_id::int and org_id = ${def:org}) as staff_name
from cc_target_group tg
left join hr_team team ON tg.pk_value = team.team_id and tg.org_id = team.org_id
where tg.org_id=${def:org} and tg.tuid = ${fld:tuid}::int
