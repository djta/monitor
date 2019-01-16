insert into cc_config
(
	tuid,
	category,
	param_value,
	param_text,
	remark,
	status,
	createdby,
	created,
	org_id
)
values
(
	${seq:nextval@seq_cc_config},
	${fld:category},
	${fld:vc_topic},
	${fld:vc_content},
	(select remark from cc_config where category = ${fld:category} limit 1),
	1,
    '${def:user}',
    {ts'${def:timestamp}'},
	${def:org}
)
