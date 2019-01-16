UPDATE hr_skill 
SET
	skill_name = ${fld:skill_name}
	,skill_scope = ${fld:skill_scope}
	,data_limit = ${fld:data_limit}
	,remark = ${fld:remark}
	,updated = {ts '${def:timestamp}'}
	,updatedby = '${def:user}' 
WHERE skill_id = ${fld:tuid} and org_id = ${def:org}
