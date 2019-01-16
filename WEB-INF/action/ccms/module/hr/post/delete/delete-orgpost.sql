delete from 
	hr_post_staff
where 
    org_post_id in (select tuid from hr_org_post where post_id = ${fld:id})
