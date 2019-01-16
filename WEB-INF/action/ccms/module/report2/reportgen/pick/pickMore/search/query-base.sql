select
	t.domain_value as id
	,t.domain_text_${def:locale} as description
	,case when nvl(t.parent_namespace,'') <> '' and nvl(t.parent_domain_value,'') <> '' 
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
	(t.subject_id is null or t.subject_id=(select def_subject_id from hr_staff s where userlogin = '${def:user}'))

order by
	reference,description