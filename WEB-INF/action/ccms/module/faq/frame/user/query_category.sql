select
	distinct t.tuid,
	t.category_name 
from 
	t_faq_category t 
where
	t.staff_id = '${def:user}'
order by t.tuid asc