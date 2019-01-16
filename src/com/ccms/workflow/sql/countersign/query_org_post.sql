select
	p.tuid as org_post_id
from
	hr_org_post p
where
	p.post_id = ${countersign_post}
and
	exists(
		select 1 from hr_org c
		where 
			c.org_id = p.org_id
		and
			exists(
				select 1 from hr_org where org_id = ${org_id} and charindex(org_path,c.org_path)>=1
			)
	)