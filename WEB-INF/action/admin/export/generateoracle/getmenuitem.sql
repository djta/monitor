select s.path, m.title, mi.position
from ${schema}s_menu_item mi, ${schema}s_service s, ${schema}s_menu m
where mi.menu_id = m.menu_id
and mi.service_id = s.service_id
and m.app_id = ${fld:webapp} 
and s.app_id = ${fld:webapp}