select
	t.domain_value as id
	,t.domain_text_${def:locale} as description
,case when case when t.parent_namespace is null then '' else t.parent_namespace end  <> '' and (t.parent_domain_value is not null and t.parent_domain_value <> '') 
		then concat('[',(select domain_text_${def:locale} from t_domain where namespace=t.parent_namespace and domain_value=t.parent_domain_value) , ']&nbsp;')
		else '' end as reference
from
	t_domain t
where
	lower(t.namespace) = lower(${fld:namespace})
and
	t.is_enabled = '1'
and
	upper (t.domain_text_cn) like upper (${fld:name})
and
	(t.subject_id is null or t.subject_id=${def:subject})

order by
	reference,description