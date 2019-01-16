INSERT INTO ${schema}s_panel (panel_id, orden, icon_path, service_id, app_id)
select ${seq:nextval@${schema}seq_s_panel}, ${fld:orden}, ${fld:icon_path}, s.service_id, ${seq:currval@${schema}seq_application}
from 
${schema}s_service s 
where s.path = ${fld:path} and s.app_id = ${seq:currval@${schema}seq_application};
