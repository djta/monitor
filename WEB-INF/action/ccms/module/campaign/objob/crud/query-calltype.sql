select
    domain_value	as	call_type
    ,domain_text_${def:locale}	as	call_type_name
from 
    t_domain
where
    is_enabled = '1' and lower(namespace) = 'outboundtype'
order by 
    domain_value