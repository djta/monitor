select
	b.tuid
	,b.field_code
	,b.col_name
	,b.domain_namespace as namespace
	,b.field_type
from 
	t_import_field b
where 
	b.tab_id = ${fld:tab_id}
	
	${filter}
	${orderby}