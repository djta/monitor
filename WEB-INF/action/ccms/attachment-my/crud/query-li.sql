select
	domain_value,
	domain_text_cn
from
	t_domain
where 
	namespace='FileShowType'
order by 
	domain_value