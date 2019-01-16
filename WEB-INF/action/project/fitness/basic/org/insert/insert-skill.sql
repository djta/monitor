INSERT INTO hr_skill(
	skill_id
	,skill_name
	,skill_scope
	,org_id
	,remark
	,created
	,createdby
	,data_limit
) 
VALUES( 
	${seq:nextval@seq_hr_skill}
	,${fld:skill_name}
	,${fld:skill_scope}
	,${seq:currval@seq_hr_org} 
	,${fld:remark}
	,{ts '${def:timestamp}'}
	,'${def:user}'
	,${fld:data_limit} 
)
