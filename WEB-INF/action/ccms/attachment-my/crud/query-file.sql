select
	domain_value as val,
	domain_text_cn as name
from
	t_domain
where 
	namespace='FileServiceType'
order by 
	domain_value