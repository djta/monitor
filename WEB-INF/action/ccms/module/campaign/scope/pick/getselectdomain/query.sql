select
	t.domain_text_${def:locale} as domain_text,
	t.domain_value 
from t_domain t 
where t.namespace = ${fld:child_namespace}
and (case when ${fld:parent_namespace} is not null and '' != ${fld:parent_namespace} then t.parent_namespace = ${fld:parent_namespace} else true end) 
and (case when ${fld:parent_value} is not null and '' != ${fld:parent_value} then t.domain_value = ${fld:parent_value} else true end)
and t.is_enabled = '1'
order by t.show_order, t.domain_text_${def:locale}
