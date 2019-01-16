select 
	p.tuid    as  id
	, p.field_name_cn as description
	, p.field_code as name
	, ${fld:code_type} as code_type
from 
	t_field p
where
	p.table_id = ${fld:table_id}
	
	${filter}
	${orderby}
