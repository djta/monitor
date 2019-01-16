select
	t.org_id,
	case when t.pid is null then 0 else t.pid end as pid,
	t.org_name,
	t.org_type
from
	hr_org t 
where 
	t.tenantry_id =${def:tenantry}
and
	exists(
		select 1 from hr_org where org_type = '1' and charindex(t.org_path,org_path)>=1
	)
order by 
	t.show_order asc