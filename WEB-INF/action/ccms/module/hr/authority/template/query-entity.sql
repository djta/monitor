select
	t.tuid as entity_id
	,t.entity_type
from
	hr_authority_entity t
where
	t.authority_id = ${authority_id}