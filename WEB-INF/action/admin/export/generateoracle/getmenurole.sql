select m.title, r.rolename
from ${schema}s_menu_role mr, ${schema}s_menu m, ${schema}s_role r
where mr.role_id = r.role_id
and mr.menu_id = m.menu_id
and m.app_id = ${fld:webapp} 
and r.app_id = ${fld:webapp}

