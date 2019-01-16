select 
	t.field_code
	,t.field_name_${def:locale}  as  field_name
,case when t.domain_namespace is null then '' else t.domain_namespace end as domain_namespace
	,t.field_type
from 
	t_form f
	inner join t_table b
	on b.tuid = f.table_id
	inner join t_field t
	on t.table_id = b.tuid
where 
	f.tuid = ${fld:form_id}
	
	${filter}