insert into hr_org(
	org_id
	,pid
	,org_path
	,org_name

	,remark
	,show_order
	,short_name
	,is_deleted
) values(
	${seq:nextval@seq_hr_org}
	,${fld:pid}
	,concat(${seq:currval@seq_hr_org},';',${fld:p_org_path})
	,${fld:org_name}
	,${fld:remark}
	,${fld:show_order}
	,${fld:short_name}
	,'0'
)
