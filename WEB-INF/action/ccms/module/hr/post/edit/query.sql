select
	post_id as tuid,
	post_name,
	show_order
FROM
	hr_post p
WHERE
	p.post_id=${fld:id}