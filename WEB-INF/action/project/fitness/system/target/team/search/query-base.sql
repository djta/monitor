select 
	tg.tuid,
	team.team_id,
	team.team_name as val,
	team.skill_scope,
	concat(tg.target_month, '-', tg.target_year) as yearmonth,
	tg.target_type
from cc_target_group tg
inner join hr_team team ON tg.pk_value = team.team_id and tg.org_id = team.org_id
where tg.org_id=${def:org} and tg.target_type=1
${filter}
${orderby}