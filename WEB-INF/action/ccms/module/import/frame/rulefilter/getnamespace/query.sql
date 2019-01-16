select 
	d.domain_text_${def:locale} as domain_text
	,d.domain_value
from 
	t_domain d
where 
	lower(d.namespace) = lower(${fld:namespace})
and
	d.is_enabled = '1'