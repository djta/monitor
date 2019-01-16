update 
	hr_headcount
set
	org_post_id = null
where
	org_post_id in (select tuid from hr_org_post where post_id = ${fld:id})
