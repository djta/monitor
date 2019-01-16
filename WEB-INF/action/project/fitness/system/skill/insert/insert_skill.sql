INSERT INTO hr_skill(
	skill_id
	,skill_name
	,org_id
	,remark
	,created
	,createdby
) VALUES(
	${seq:nextval@seq_hr_skill}
	,${fld:skill_name}
	,${def:org}
	,${fld:remark}
	,{ts '${def:timestamp}'}
	,'${def:user}'
)
