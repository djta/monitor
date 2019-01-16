select s.path, r.rolename
from ${schema}s_service_role sr, ${schema}s_service s, ${schema}s_role r
where sr.role_id = r.role_id
and sr.service_id = s.service_id
and s.app_id = ${fld:webapp} and r.app_id = ${fld:webapp}