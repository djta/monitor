select
	distinct
      s.description_${def:locale} as title,
    m.panel_id as id,
    m.orden,
    s.path,m.icon_path iconpath
from
    ${schema}s_panel m,
    ${schema}s_service_role sr,
    ${schema}s_user u,
    ${schema}s_user_role ur,
    ${schema}s_service s
where
    u.userlogin = '${def:user}'
and
    s.service_id = m.service_id
and
    sr.service_id = s.service_id
and
    ur.role_id = sr.role_id
and
    ur.user_id = u.user_id
order by 
    m.orden