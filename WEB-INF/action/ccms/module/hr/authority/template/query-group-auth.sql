select
	t.authority_id
	,t.access_type
	,t.logic_type
from
	hr_authority_relation t 
	inner join hr_authority a
	on t.authority_id = a.tuid
where
	t.group_id = ${group_id}