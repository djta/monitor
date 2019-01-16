with menus as(
select distinct
	m.title_${def:locale} as title,
	m.menu_id as id,
	m.position as p_position,
	m.position,
	0 pid,
	s.path as path,
	'' logopath
from
	${schema}s_menu m left join ${schema}s_service s on s.service_id= m.service_id	,
	${schema}s_menu_role mr,
	${schema}s_user u,
	${schema}s_user_role ur,
	${schema}s_application a,
	${schema}s_role r
	
where
	a.app_alias = '${req:dinamica.security.application}'
and
	u.userlogin = '${def:user}'
and
	r.app_id = a.app_id
and
	ur.role_id = r.role_id
and
	ur.user_id = u.user_id
and
	mr.role_id = ur.role_id
and
	m.menu_id = mr.menu_id
and
	m.parentmenu_id is null
)
select * from(
select
	distinct
	s.description_${def:locale} as title,
	m.menu_item_id as id,
	pm.position as p_position,
	m.position,
	m.menu_id as pid,
	s.path as path,
	m.logo_path as logopath
from
	${schema}s_menu_item m,
	${schema}s_service_role sr,
	${schema}s_user u,
	${schema}s_user_role ur,
	${schema}s_service s,menus pm
where
	u.userlogin = '${def:user}'
and 
	m.menu_id = pm.id
and
	s.service_id = m.service_id
and
	sr.service_id = s.service_id
and
	ur.role_id = sr.role_id
and
	ur.user_id = u.user_id

union  

SELECT * from menus

) s
	order by s.p_position,s.position