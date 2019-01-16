select p.*, s.path from ${schema}s_panel p,${schema}s_service s
 where p.app_id = ${fld:webapp}
 and s.app_id = ${fld:webapp}
 and s.service_id = p.service_id
 
