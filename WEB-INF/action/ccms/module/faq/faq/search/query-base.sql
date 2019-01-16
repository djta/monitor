select 
    tuid
    ,show_name
    ,'' as superior
    ,lable
    ,concat('点击查看','') as content
    ,case when is_expired='1' then concat('已经过期','') else concat('未过期','') end as is_expired
from 
    t_faq t
where 
	(t.is_delete = '0' or t.is_delete is null or t.is_delete = '')
and 
	t.tenantry_id = ${def:tenantry}
    
	${filter}
 
${orderby}
