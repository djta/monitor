select 
	t.tuid    as  field_id
	, t.field_name_${def:locale} as field_code
	, concat((case when is_virtual_type='0' then '' else '@' end) , t.field_name) as alias
	, t.field_code_alias
from 
	t_form fm
	inner join t_field t
	on t.table_id = fm.table_id
where 
	fm.tuid = ${fld:form_id}
order by 
	t.show_order
