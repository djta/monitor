select
	p.tuid,
	p.post_id,
	p.show_order,
	p.org_post_name,
	p.pid
FROM
	hr_org_post p
WHERE
	p.tuid=${fld:id}