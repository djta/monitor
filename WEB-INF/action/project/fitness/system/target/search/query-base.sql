select 
	tg.tuid,
	skill.skill_id,
	concat(tg.target_month, '月份', skill.skill_name, '任务目标') as val,
	skill.skill_scope,
	concat(tg.target_month, '-', tg.target_year) as yearmonth,
	tg.target_type
from cc_target_group tg
inner join hr_skill skill ON tg.pk_value = skill.skill_id and tg.org_id = skill.org_id
where tg.org_id=${def:org} and tg.target_type=0
${filter}
${orderby}