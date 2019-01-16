select 
	tg.tuid,
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
	tg.target_month||'月份'||skill.skill_name||'任务目标' as val,
	tg.target_year||'-'||tg.target_month as yearmonth,
	(select count(1) from hr_staff 
		inner join (select user_id from hr_staff_skill where skill_id = tg.pk_value) hs
			on hs.user_id =hr_staff.user_id
		where hr_staff.org_id = ${def:org}) as staffnum
from cc_target_group tg
left join hr_skill skill ON tg.pk_value = skill.skill_id and tg.org_id = skill.org_id
where tg.org_id=${def:org} and tg.tuid = ${fld:tuid}
