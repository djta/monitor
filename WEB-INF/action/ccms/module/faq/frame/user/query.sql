select
	distinct t.tuid,
	t.superior_id,
	t.show_name 
from 
	t_faq t 
	left join t_faq_skill s
	on t.tuid = s.faq_id
	left join hr_skill k
	on s.skill_id = k.skill_id
	left join hr_staff_skill a
	on k.skill_id = a.skill_id
where
	(t.end_date >= {d '${def:date}'} or t.end_date is null)
and
	(t.start_date <= {d '${def:date}'} or t.start_date is null)
and
	t.faq_type = '0'
and
	(t.is_expired = '0' or t.is_expired is null)
and
    t.tenantry_id = ${def:tenantry}
and
	(t.is_delete = '0' or t.is_delete is null or t.is_delete = '')
order by t.superior_id asc