select 
	p.tuid    as  id
	, p.document_name as description
	, d.domain_text_${def:locale} as document_type
from 
	t_document p
	inner join t_table t on p.table_id = t.tuid
	left join t_domain d
	on p.document_type = d.domain_value and d.namespace='ConfigType'
where
	p.subject_id = ${fld:subject_id}
	${filter}
	${orderby}