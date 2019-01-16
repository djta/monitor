select 
	tuid    as  id
	, table_alias  as description
from 
	t_table
where 
    subject_id = ${fld:subject_id}
order by 
	id
