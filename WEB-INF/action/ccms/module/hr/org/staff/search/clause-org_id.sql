and
(
	t.org_id = ${fld:org_id}
or
	exists(
		select 1 from hr_org_post p
				inner join hr_post_staff f
				on p.tuid = f.org_post_id
			where
				p.org_id = ${fld:org_id}
			and
				f.userlogin = t.userlogin
	)
)