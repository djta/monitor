select
	c.entity_scope
	,c.entity_values
	,w.pk_value
	,c.pk_values
from	
	os_wfentry w
	inner join os_wfm_countersign c
	on w.pk_value = c.tuid
where
	id = ${id}