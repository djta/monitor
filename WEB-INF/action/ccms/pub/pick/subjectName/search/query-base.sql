select 
	p.tuid    as  id
	, p.subject_name as description
from 
	t_subject p
where
	p.is_deleted = '0'

	${filter}
	${orderby}
