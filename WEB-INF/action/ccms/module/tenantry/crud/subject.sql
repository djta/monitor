select
	tuid,
	subject_name
from
	t_subject
where 
	is_deleted = '0'
order by
	created