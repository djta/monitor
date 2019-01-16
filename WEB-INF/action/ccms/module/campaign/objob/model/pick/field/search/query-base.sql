select 
	f.field_code    as  id
	,f.field_name_${def:locale} as description
	,f.field_code
from 
	t_field f
	left join T_FORM_GRID_FIELD g
	on f.tuid = g.field_id
where
	f.table_id = ${fld:table_id}
and 
	g.form_id = ${fld:form_id}
	
	${filter}