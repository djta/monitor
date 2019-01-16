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
(
	select 
		${seq:nextval@seq_cc_config},
		category,
		param_value,
		param_text,
		remark,
		status,
	    '${def:user}',
	    {ts'${def:timestamp}'},
		${def:org}
	from cc_config
	where (case when ${fld:crud} ='1' or ${fld:crud} ='5' then 1=1 else tuid != ${fld:cnfg_id}  end)
	and category = ${fld:category} and org_id = (select org_id from hr_org where pid=0)
)
