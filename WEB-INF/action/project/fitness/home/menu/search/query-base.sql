select 
	t.tuid,
	t.pid,
	t.menu_path,
	t.menu_grade,
	t.icon_path,
	t.icon_path2,
	t.uri,
	(case when t.uri is not null and t.uri != '' then 'display:block;' else 'display:none;' end) as isshowbtn,
	t.menu_name,
	t.show_order,
	t.created,
	(case t.menu_type when 1 then '首页图表' else '普通菜单' end) as menu_type,
	f.name AS createdby 
from hr_menu t 
left join hr_staff f on f.userlogin = t.createdby 
where t.pid = ${fld:_pid} and t.is_deleted = 0 
	${filter}
	${orderby}