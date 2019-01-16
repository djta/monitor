select
	p.tuid    as  id
	, p.table_name as description
	, p.table_code as name
	, d.domain_text_${def:locale} as table_type
from 
	t_table p
	left join t_domain d
	on p.table_type = d.domain_value and d.namespace='ConfigType'
where
	p.subject_id = ${fld:subject_id}
	
	${filter}
	${orderby}