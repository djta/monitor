select 
	1 as table_id 
from 
	t_field
where 
	table_id = ${fld:table_id}
and
	(deleted='0' or deleted is null)