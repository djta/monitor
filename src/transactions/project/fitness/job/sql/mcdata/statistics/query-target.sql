select 
	ts.orderfee_target
from cc_target_staff ts
left join cc_target_group tg on ts.targetgroupid = tg.tuid and ts.org_id = tg.org_id
where ts.org_id = ${fld:org_id} and ts.userlogin = ${fld:userlogin}
	and to_char(concat(tg.target_year, '-', tg.target_month , '-01')::date,'YYYYMM')=
		to_char('${def:date}'::date - interval '1 month','YYYYMM')
	and ts.target_type in (SELECT config.param_value::int FROM cc_config config
		WHERE config.category = 'Taskgoal' and config.org_id = (case when 
		not exists(select 1 from cc_config c where c.org_id = ${fld:org_id} and c.category=config.category) 
		then (select org_id from hr_org where pid is null or pid = 0) else ${fld:org_id} end))

