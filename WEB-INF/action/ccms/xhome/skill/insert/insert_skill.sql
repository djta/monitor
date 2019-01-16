INSERT INTO hr_skill(
	skill_id
	,skill_name
	,tenantry_id
	,remark
	,created
	,createdby
) VALUES(
	${seq:nextval@seq_hr_skill}
	,${fld:skill_name}
	,${def:tenantry}
	,${fld:remark}
	,{ts '${def:timestamp}'}
	,'${def:user}'
)
