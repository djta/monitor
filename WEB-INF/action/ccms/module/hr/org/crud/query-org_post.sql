select
	t.org_post_name,
	t.tuid
from
hr_org_post t
inner join hr_post p on t.post_id = p.post_id
where
 tenantry_id = ${def:tenantry}

