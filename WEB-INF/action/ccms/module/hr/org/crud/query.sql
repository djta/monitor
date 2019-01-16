select
	t.org_id,
	case when t.org_id=f.org_id then null else t.pid end as pid,
	t.org_path,
	t.org_name 
from
	hr_org t
left join hr_staff f on f.org_id=t.org_id and f.userlogin = '${def:user}'
where 
	t.tenantry_id = ${def:tenantry} AND t.is_deleted = '0' 
AND	exists (select 1 from hr_org os2 ,hr_staff s2 where os2.org_id=s2.org_id and  CHARINDEX(os2.org_path ,t.org_path)>=1 and s2.userlogin='${def:user}')
order by 
	t.show_order,t.org_name asc
