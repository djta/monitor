select 
	tuid    as  id
	, term_name  as description
from 
	t_term
where 
    subject_id = ${fld:subject_id}
order by 
	id
