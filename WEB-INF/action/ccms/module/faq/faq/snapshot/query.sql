select
	case when f.is_expired='1' then '过期' else '未过期' end as is_expired
    ,f.create_date
from 
    t_faq f 
where
    f.tuid = ${fld:faq_id}
