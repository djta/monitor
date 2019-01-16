select 
    a.description,
	g.group_id,
	g.app_id,
	g.group_name
from
	${schema}s_service_group g
inner join 
     ${schema}s_application a
 on
     g.app_id=a.app_id
where
    1 = 1

${filter}
${orderby}