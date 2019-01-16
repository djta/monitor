select 
	p.tuid    as  id
	, p.wfm_name as description
	, t.table_alias as name
	, d.domain_text_${def:locale} as wfm_type
from 
	os_wfm p
	inner join t_table t
	on p.table_id = t.tuid
	left join t_domain d
	on p.wfm_type = d.domain_value and d.namespace='ConfigType'
where
	p.tenantry_id = ${def:tenantry}
and
	p.wfm_status = '1'

	${filter}
	${orderby}