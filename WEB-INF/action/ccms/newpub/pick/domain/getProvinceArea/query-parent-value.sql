(
SELECT
	t.parent_domain_value
	,t.parent_domain_value2
FROM
	t_domain t
WHERE
	t.namespace = 'City'
and
	t.domain_value = ${fld:city}
and
	t.is_enabled = '1'
order by 
    t.show_order
    ,t.domain_text_${def:locale}
)
union
(
select
	'' as parent_domain_value
	,'' as parent_domain_value2
from
	dual
where
	${fld:city} is null
)