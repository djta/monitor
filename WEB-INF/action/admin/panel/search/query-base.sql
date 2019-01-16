select
	p.panel_id,
	p.icon_path,
	b.path,
	b.description,
	p.orden
from 
	${schema}s_panel p,
	${schema}s_service b,
	${schema}s_application a
where
	p.app_id=${fld:app_id} 
and
	p.service_id=b.service_id 
and
	a.app_id=p.app_id

	${filter}

	${orderby}