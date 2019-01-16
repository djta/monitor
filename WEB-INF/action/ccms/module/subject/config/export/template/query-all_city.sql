select 
	t2.domain_text_cn province, t1.domain_text_cn city 
from 
	(select t.* from t_domain t where t.namespace='City') t1 
	LEFT JOIN (select t.* from t_domain t where t.namespace='Province') t2 
	on t1.parent_domain_value = t2.domain_value 
	
ORDER BY t2.domain_text_cn ;