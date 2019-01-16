select
    f.tuid,
	f.show_name,
	(select count(1) from t_faq_click t where t.faq_id=f.tuid and t.click_time >= {d '${def:date}'}) as total
from
	t_faq f
where
	f.tenantry_id=${def:tenantry}
and
	(select count(1) from t_faq_click t where t.faq_id=f.tuid and t.click_time >= {d '${def:date}'})>0
and 
	(f.is_delete = '0' or f.is_delete is null or f.is_delete = '')
order by 
	total
