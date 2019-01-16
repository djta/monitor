select
	distinct t.tuid,
	f.category_id,
	t.show_name 
from 
	t_faq t 
	inner join t_faq_favorite f
	on t.tuid = f.faq_id
where
	f.staff_id = '${def:user}'
	and t.tenantry_id=${def:tenantry}
	and (t.is_delete = '0' or t.is_delete is null or t.is_delete = '')
order by f.category_id asc