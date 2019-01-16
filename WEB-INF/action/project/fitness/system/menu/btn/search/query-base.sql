select 
	t.tuid,
	t.btn_name,
	t.btn_id,
	t.created,
	f.name AS createdby 
from hr_menu_btn t 
left join hr_staff f on f.userlogin = t.createdby and f.tenantry_id = t.tenantry_id and f.org_id = t.org_id 
where t.menu_id = ${fld:_menu_id} and t.is_deleted = 0 
and t.tenantry_id = ${def:tenantry} and t.org_id = ${def:org} 
	${filter}
	${orderby}