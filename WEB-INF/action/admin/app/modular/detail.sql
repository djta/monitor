select distinct
	s.description,
	s.path,
	m.position
from
	${schema}s_menu_item m,
	${schema}s_service_role sr,
	${schema}s_service s
where
	m.menu_id = ${fld:menu_id}
and
	s.service_id = m.service_id
and
	sr.service_id = s.service_id

order by m.position
