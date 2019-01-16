SELECT
	p.tuid as post_id,
	p.org_post_name as post_name
FROM
	hr_org_post p
WHERE
	p.org_id = ${fld:org_id}
and

NOT EXISTS (
	SELECT
		1
	FROM
		hr_authority_list
	WHERE
		hr_authority_list.entity_value = cast(p.tuid as varchar(32))
)