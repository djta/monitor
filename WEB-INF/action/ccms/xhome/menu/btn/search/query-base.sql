select 
	t.tuid,
	t.btn_name,
	t.btn_id,
	t.created,
	f.name AS createdby 
from hr_menu_btn t 
left join hr_staff f on f.userlogin = t.createdby 
where t.menu_id = ${fld:_menu_id} and t.is_deleted = 0 
	${filter}
	${orderby}