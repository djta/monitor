select 
	tuid    as  id
	, form_name  as description
from 
	t_form
where 
    table_id = ${fld:table_id}
order by 
	id
