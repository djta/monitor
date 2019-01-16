select distinct
	m.title,
	m.menu_id,
	m.position
from
	${schema}s_menu m,
	${schema}s_menu_role mr,
	${schema}s_application a,
	${schema}s_role r
where
	a.app_id = ${fld:app_id}
and
	r.app_id = a.app_id
and
	m.menu_id = mr.menu_id
and
	m.app_id = a.app_id
order by 
	m.position
