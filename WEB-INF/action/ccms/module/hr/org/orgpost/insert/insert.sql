INSERT	INTO
hr_org_post
(
	tuid
	,org_id
	,post_id
	,show_order
	,pid
	,org_post_name
	,created
	,createdby
)
VALUES
(
	 ${seq:nextval@seq_hr_org_post}
	,${fld:org_id}
	,${fld:post_id}
	,${fld:show_order}
	,${fld:pid}
	,${fld:org_post_name}
	,{ts '${def:timestamp}'}
	,'${def:user}'
)