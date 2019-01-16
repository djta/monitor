select 
	p.tuid    as  id
	, p.form_name as description
	, t.table_alias as name
	, d.domain_text_${def:locale} as form_type
from 
	t_form p
	inner join t_table t
	on p.table_id = t.tuid
	left join t_domain d
	on p.form_type = d.domain_value and d.namespace='ConfigType'
where
	p.table_id = ${fld:table_id}
	
	${filter}

order by 
	p.form_name
