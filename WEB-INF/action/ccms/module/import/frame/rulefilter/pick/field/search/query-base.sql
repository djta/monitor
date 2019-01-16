select
	t.field_code
	,t.field_name_${def:locale}  as  field_name
	,t.domain_namespace as namespace
	,t.field_type
from 
	t_import_table f
	inner join t_table b
	on b.tuid = f.table_id
	inner join t_field t
	on t.table_id = b.tuid
where 
	f.tuid = ${fld:tab_id}
	
	${filter}
	${orderby}