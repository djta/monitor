select 
	distinct 
	t.domain_value as id 
	,t.namespace as namespace
	,t.domain_text_${def:locale} as description
from 
	t_domain t
where
	upper (t.domain_text_${def:locale}) like upper (${fld:name})
and
	lower(t.namespace) like lower(${fld:namespace})
and
	(
		t.subject_id is null
		or
		t.subject_id = ${def:subject}
	)
