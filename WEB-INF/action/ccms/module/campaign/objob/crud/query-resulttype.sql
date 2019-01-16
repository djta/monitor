select
    domain_value	as	result_type
    ,domain_text_${def:locale}	as	result_type_name
from
    t_domain
where
    is_enabled = '1' and lower(namespace) = 'dropsolvedresult'
order by
    domain_value