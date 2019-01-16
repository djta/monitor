select
	t.org_id,
	case when t.pid is null then 0 else t.pid end as pid,
	t.org_name
from
	hr_org t 
where
	t.tenantry_id =${def:tenantry}
order by 
	t.show_order asc