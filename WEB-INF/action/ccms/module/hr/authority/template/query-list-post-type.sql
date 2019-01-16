select
	g.org_id
	,g.org_name
	,op.tuid as post_id
	,op.org_post_name as post_name
	,f.userlogin
	,f.name as staff_name
from
	hr_authority_list t
	inner join hr_post p on t.entity_value = cast(p.post_id as varchar)
	inner join hr_org_post op on op.post_id = p.post_id
	left join hr_post_staff ps on ps.org_post_id = op.tuid
	inner join hr_staff f on f.userlogin = ps.userlogin
	inner join ${schema}s_user u on f.userlogin = u.userlogin and u.enabled = 1
	left join hr_org g on f.org_id = g.org_id
where
	t.entity_id = ${entity_id}