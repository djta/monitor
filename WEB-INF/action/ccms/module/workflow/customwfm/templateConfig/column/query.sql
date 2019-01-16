select 
	tuid
	,field_name_${def:locale}  as  field_name
from 
	t_field 
where 
  table_id = ${fld:table_id}

order by 
	tuid
