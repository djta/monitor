select
	t.tuid as org_post_id
	,case when t.pid is not null then t.pid else 0 end as pid
	,t1.org_post_name as pname
	,t.org_post_name
	,tt.post_name
	,t.show_order
	,'child' as op_type
from
	hr_org_post t 
	left join hr_post tt on t.post_id=tt.post_id
	left join hr_org_post t1 on t1.tuid=t.pid
where
	t.org_id = ${fld:org_id}
	${filter}

order by 
	op_type,show_order,t.org_post_name