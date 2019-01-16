INSERT	INTO
	hr_post
(
	post_id
	,post_name
	,show_order
	,created
	,createdby
	,tenantry_id
)
VALUES
(
	 ${seq:nextval@seq_hr_post}
	,${fld:post_name}
	,${fld:show_order}
	,{ts '${def:timestamp}'}
	,'${def:user}'
	,${def:tenantry}
)               