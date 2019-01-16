select 
	p.tuid    as  id
	, p.report_name_cn as description
	, t.table_alias as name
	, d.domain_text_${def:locale} as group_type
from 
	t_report p
	inner join t_table t
	on p.table_id = t.tuid
	left join t_domain d
	on p.group_type = d.domain_value and d.namespace='ConfigType'
where
	p.table_id = ${fld:table_id}
	
	${filter}
	${orderby}