select 
	s.service_id, 
	s.path, 
	s.description,
	s.description_en,
	s.description_cn,
	p.group_name
from
	${schema}s_service s
left join 
	${schema}s_service_group p
on 
	s.group_id = p.group_id
where 
	s.app_id = ${fld:app_id}
order by 
	p.group_name