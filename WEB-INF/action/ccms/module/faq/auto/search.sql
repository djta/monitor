select 
	s.search_key,count(*) weight 
from 
	t_faq_search_word s 
where 
	s.search_key ~* ${fld:q} 
group by 
	s.search_key 
order by 
	weight desc