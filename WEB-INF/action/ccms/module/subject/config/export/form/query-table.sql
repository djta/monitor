select
	distinct
	t.*
from 
	t_table t
	inner join t_form f
	on t.tuid = f.table_id
where 
	f.tuid = ${fld:form_id}
