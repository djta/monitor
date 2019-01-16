(
SELECT
	t.parent_domain_value
FROM
	t_domain t
WHERE
	t.namespace = ${fld:namespace}
and
	t.domain_value = ${fld:domain_value}
and
	t.parent_namespace = ${fld:parent_namespace}
and
	t.is_enabled = '1'
and
	t.subject_id=${def:subject}
order by 
    t.show_order
    ,t.domain_text_${def:locale}
)
union
(
select
	'' as parent_domain_value
from
	dual
where
	${fld:domain_value} is null
)