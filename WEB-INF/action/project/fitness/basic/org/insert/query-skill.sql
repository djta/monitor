SELECT 
	skill_id
	,skill_name
	,skill_scope
	,remark
	,data_limit 
FROM hr_skill 
WHERE org_id = ${fld:pid}
